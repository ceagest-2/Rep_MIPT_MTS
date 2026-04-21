package com.mipt.uriilesnikov;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Layer1")
class SecurityAndExternalApiL1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginReturnsJwtToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "user",
                                "password", "password"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value(not(isEmptyOrNullString())));
    }

    @Test
    void profileWithoutTokenReturns401Json() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    void profileWithUserTokenReturns200() throws Exception {
        String token = loginAndGetToken("user", "password");

        mockMvc.perform(get("/api/v1/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    void docsWithUserTokenReturns403() throws Exception {
        String token = loginAndGetToken("user", "password");

        mockMvc.perform(get("/api/v1/docs")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void docsWithReaderTokenReturns200() throws Exception {
        String token = loginAndGetToken("reader", "password");

        mockMvc.perform(get("/api/v1/docs")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("Resilient Secure HTTP Gateway"));
    }

    @Test
    void traceIdIsEchoedBackInResponseHeader() throws Exception {
        String token = loginAndGetToken("user", "password");

        mockMvc.perform(get("/api/v1/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header("X-Trace-Id", "trace-test-123"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Trace-Id", "trace-test-123"));
    }

    @Test
    void externalCrudSupportsCreatedNoContentAndProblemDetails() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/external/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "demo",
                                "description", "external test",
                                "completed", false
                        ))))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/external/v1/tasks/")))
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long id = createdNode.get("id").asLong();

        mockMvc.perform(get("/external/v1/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(delete("/external/v1/tasks/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/external/v1/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Task not found"))
                .andExpect(jsonPath("$.detail").value(containsString("id=" + id)));
    }

    @Test
    void externalUnstable429ReturnsRetryAfterHeader() throws Exception {
        mockMvc.perform(get("/external/v1/unstable").queryParam("mode", "429"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(HttpHeaders.RETRY_AFTER, "5"));
    }

    @Test
    void externalUnstableHtmlReturnsTextHtml() throws Exception {
        mockMvc.perform(get("/external/v1/unstable").queryParam("mode", "html"))
                .andExpect(status().isBadGateway())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString(MediaType.TEXT_HTML_VALUE)));
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return jsonNode.get("accessToken").asText();
    }
}
