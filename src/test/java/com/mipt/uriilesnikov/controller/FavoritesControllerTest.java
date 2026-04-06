package com.mipt.uriilesnikov.controller;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void favorites_shouldBeStoredInSession() throws Exception {
        long taskId = createTask("Favorite task");
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/{taskId}", taskId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(taskId));

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[0].id").value(taskId));

        mockMvc.perform(delete("/api/favorites/{taskId}", taskId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addFavorite_shouldFailForUnknownTask() throws Exception {
        mockMvc.perform(post("/api/favorites/{taskId}", 999999L).session(new MockHttpSession()))
                .andExpect(status().isNotFound());
    }

    private long createTask(String title) throws Exception {
        String payload = """
                {
                  "title": "%s",
                  "description": "Description",
                  "dueDate": "%s",
                  "priority": "LOW",
                  "tags": ["tag"]
                }
                """.formatted(title, LocalDate.now().plusDays(1));

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
