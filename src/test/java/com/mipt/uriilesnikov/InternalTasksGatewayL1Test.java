package com.mipt.uriilesnikov;

import com.mipt.uriilesnikov.dto.TaskUpsertRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=18080",
                "app.external.base-url=http://localhost:18080/external/v1",
                "resilience4j.ratelimiter.instances.externalApi.limit-for-period=1000",
                "resilience4j.ratelimiter.instances.externalApi.limit-refresh-period=1s"
        }
)
@Tag("Layer1")
class InternalTasksGatewayL1Test {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void gatewayCrudFlowWorksAndTranslates404FromExternalApi() {
        String token = loginAndGetToken("user", "password");

        HttpHeaders authHeaders = bearerJsonHeaders(token);
        TaskUpsertRequest request = new TaskUpsertRequest("gateway-task", "created through internal api", false);

        ResponseEntity<Map> createResponse = restTemplate.exchange(
                "/api/v1/tasks",
                HttpMethod.POST,
                new HttpEntity<>(request, authHeaders),
                Map.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getHeaders().getLocation());
        assertNotNull(createResponse.getBody());

        Number createdIdRaw = (Number) createResponse.getBody().get("id");
        assertNotNull(createdIdRaw);
        long createdId = createdIdRaw.longValue();

        ResponseEntity<Map> getResponse = restTemplate.exchange(
                "/api/v1/tasks/{id}",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                Map.class,
                createdId
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(createdId, ((Number) getResponse.getBody().get("id")).longValue());

        ResponseEntity<Map> listResponse = restTemplate.exchange(
                "/api/v1/tasks?completed=false&limit=10",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                Map.class
        );

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertNotNull(listResponse.getBody());
        List<?> tasks = (List<?>) listResponse.getBody().get("tasks");
        assertNotNull(tasks);
        assertTrue(tasks.size() <= 10);

        ResponseEntity<Map> deleteResponse = restTemplate.exchange(
                "/api/v1/tasks/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders),
                Map.class,
                createdId
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertEquals(Boolean.TRUE, deleteResponse.getBody().get("success"));

        ResponseEntity<Map> notFoundResponse = restTemplate.exchange(
                "/api/v1/tasks/{id}",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders),
                Map.class,
                999_999_999L
        );

        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
        assertNotNull(notFoundResponse.getBody());
        assertTrue(String.valueOf(notFoundResponse.getBody().get("message")).contains("not found"));
    }

    @Test
    void gatewayReturnsGracefulDegradationWhenExternalIsUnstable500() {
        String token = loginAndGetToken("user", "password");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/tasks/unstable?mode=500",
                HttpMethod.GET,
                new HttpEntity<>(bearerJsonHeaders(token)),
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Boolean.TRUE, response.getBody().get("degraded"));
        assertTrue(String.valueOf(response.getBody().get("message")).contains("Graceful degradation"));
    }

    private String loginAndGetToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/auth/login",
                new HttpEntity<>(Map.of("username", username, "password", password), headers),
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        return String.valueOf(response.getBody().get("accessToken"));
    }

    private HttpHeaders bearerJsonHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
