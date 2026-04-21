package com.mipt.uriilesnikov.external;

import com.mipt.uriilesnikov.dto.TaskDto;
import com.mipt.uriilesnikov.dto.TaskUpsertRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, TaskDto> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);
    private final long unstableTimeoutMs;

    public ExternalApiController(@Value("${app.external.unstable-timeout-ms}") long unstableTimeoutMs) {
        this.unstableTimeoutMs = unstableTimeoutMs;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskUpsertRequest request) {
        long id = idSequence.getAndIncrement();
        TaskDto task = TaskDto.normal(id, request.title(), request.description(),
                Boolean.TRUE.equals(request.completed()));
        tasks.put(id, task);

        URI location = URI.create("/external/v1/tasks/" + id);
        return ResponseEntity.created(location).body(task);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        TaskDto task = tasks.get(id);
        if (task == null) {
            return taskNotFound(id);
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public List<TaskDto> listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit
    ) {
        return tasks.values()
                .stream()
                .sorted(Comparator.comparing(TaskDto::id))
                .filter(task -> completed == null || completed.equals(task.completed()))
                .limit(limit)
                .toList();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        TaskDto removed = tasks.remove(id);
        if (removed == null) {
            return taskNotFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) {
        return switch (mode) {
            case "timeout" -> timeoutResponse();
            case "500" -> problemResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Simulated external failure",
                    "External service returned 500 for testing circuit breaker");
            case "429" -> rateLimitedResponse();
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>Bad Gateway</h1><p>Upstream returned HTML</p></body></html>");
            default -> problemResponse(HttpStatus.BAD_REQUEST,
                    "Unknown unstable mode",
                    "Supported modes are timeout, 500, 429, html");
        };
    }

    private ResponseEntity<?> timeoutResponse() {
        try {
            Thread.sleep(unstableTimeoutMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return problemResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Interrupted",
                    "Sleep was interrupted while simulating timeout");
        }

        TaskDto payload = TaskDto.normal(-1L, "unstable-timeout", "Delayed response completed", false);
        return ResponseEntity.ok(payload);
    }

    private ResponseEntity<ProblemDetail> rateLimitedResponse() {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS,
                "External API rate limit exceeded");
        pd.setTitle("Too many requests");
        pd.setProperty("timestamp", Instant.now().toString());

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, "5")
                .body(pd);
    }

    private ResponseEntity<ProblemDetail> taskNotFound(Long id) {
        return problemResponse(HttpStatus.NOT_FOUND,
                "Task not found",
                "Task with id=" + id + " was not found in external API");
    }

    private ResponseEntity<ProblemDetail> problemResponse(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setProperty("timestamp", Instant.now().toString());

        return ResponseEntity.status(status).body(pd);
    }
}
