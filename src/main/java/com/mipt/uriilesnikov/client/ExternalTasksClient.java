package com.mipt.uriilesnikov.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.uriilesnikov.dto.CreatedTaskResult;
import com.mipt.uriilesnikov.dto.TaskDto;
import com.mipt.uriilesnikov.dto.TaskUpsertRequest;
import com.mipt.uriilesnikov.exception.ExternalApiException;
import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final int MAX_LOG_BODY_LENGTH = 400;

    private final RestClient externalRestClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient externalRestClient, ObjectMapper objectMapper) {
        this.externalRestClient = externalRestClient;
        this.objectMapper = objectMapper;
    }

    public CreatedTaskResult createTask(TaskUpsertRequest request) {
        ResponseEntity<TaskDto> response = externalRestClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            throw toTaskNotFoundException(res, "External resource for task creation was not found");
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            throw toExternalApiException("create task", res);
                        })
                .toEntity(TaskDto.class);

        if (response.getStatusCode().value() != HttpStatus.CREATED.value()) {
            throw new ExternalApiException("Expected 201 Created but got " + response.getStatusCode().value());
        }

        TaskDto body = response.getBody();
        if (body == null) {
            body = TaskDto.normal(null, "created", null, false);
        }
        return new CreatedTaskResult(body, response.getHeaders().getLocation());
    }

    public TaskDto getTask(Long id) {
        TaskDto body = externalRestClient.get()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            throw toTaskNotFoundException(res, "Task with id=" + id + " was not found");
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            throw toExternalApiException("read task", res);
                        })
                .body(TaskDto.class);

        if (body == null) {
            throw new ExternalApiException("External API returned an empty body for task id=" + id);
        }
        return body;
    }

    public TaskListResponseEnvelope listTasks(Boolean completed, Integer limit) {
        List<TaskDto> body = externalRestClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/tasks");
                    if (completed != null) {
                        builder.queryParam("completed", completed);
                    }
                    if (limit != null) {
                        builder.queryParam("limit", limit);
                    }
                    return builder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            throw toTaskNotFoundException(res, "Task collection endpoint was not found");
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            throw toExternalApiException("list tasks", res);
                        })
                .body(new ParameterizedTypeReference<>() {
                });

        return new TaskListResponseEnvelope(body == null ? List.of() : body);
    }

    public void deleteTask(Long id) {
        ResponseEntity<Void> response = externalRestClient.delete()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            throw toTaskNotFoundException(res, "Task with id=" + id + " was not found");
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            throw toExternalApiException("delete task", res);
                        })
                .toBodilessEntity();

        if (response.getStatusCode().value() != HttpStatus.NO_CONTENT.value()) {
            throw new ExternalApiException("Expected 204 No Content but got " + response.getStatusCode().value());
        }
    }

    public TaskDto callUnstableEndpoint(String mode) {
        TaskDto body = externalRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/unstable").queryParam("mode", mode).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            throw toTaskNotFoundException(res, "Unstable endpoint was not found");
                        })
                .onStatus(HttpStatusCode::isError,
                        (req, res) -> {
                            throw toExternalApiException("call unstable endpoint", res);
                        })
                .body(TaskDto.class);

        if (body == null) {
            throw new ExternalApiException("External unstable endpoint returned empty body");
        }
        return body;
    }

    private RuntimeException toTaskNotFoundException(ClientHttpResponse response, String fallbackMessage) throws IOException {
        String detail = extractProblemDetail(readBody(response)).orElse(fallbackMessage);
        return new TaskNotFoundException(detail);
    }

    private RuntimeException toExternalApiException(String operation, ClientHttpResponse response) throws IOException {
        byte[] responseBody = readBody(response);
        MediaType contentType = response.getHeaders().getContentType();

        if (contentType != null
                && !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)
                && !MediaType.APPLICATION_PROBLEM_JSON.isCompatibleWith(contentType)) {
            log.warn("External API returned unexpected Content-Type for {}: {} body={}",
                    operation,
                    contentType,
                    truncateBody(responseBody));
        }

        String detail = extractProblemDetail(responseBody)
                .orElse("HTTP " + response.getStatusCode().value() + " from external API");

        if (response.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            String retryAfter = response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER);
            if (retryAfter != null) {
                detail = detail + " (Retry-After=" + retryAfter + "s)";
            }
        }

        return new ExternalApiException("External API failed to " + operation + ": " + detail);
    }

    private byte[] readBody(ClientHttpResponse response) throws IOException {
        if (response.getBody() == null) {
            return new byte[0];
        }
        return response.getBody().readAllBytes();
    }

    private Optional<String> extractProblemDetail(byte[] responseBody) {
        if (responseBody.length == 0) {
            return Optional.empty();
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode detailNode = jsonNode.get("detail");
            if (detailNode != null && detailNode.isTextual()) {
                return Optional.of(detailNode.asText());
            }
            return Optional.empty();
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String truncateBody(byte[] responseBody) {
        String body = new String(responseBody, StandardCharsets.UTF_8)
                .replaceAll("[\\r\\n]+", " ")
                .trim();
        if (body.length() <= MAX_LOG_BODY_LENGTH) {
            return body;
        }
        return body.substring(0, MAX_LOG_BODY_LENGTH) + "...(truncated)";
    }

    public record TaskListResponseEnvelope(List<TaskDto> tasks) {
    }
}
