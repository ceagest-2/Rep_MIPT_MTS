package com.mipt.uriilesnikov;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "app.upload-dir=target/test-uploads"
})
class ApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void taskCrudAndHeaders_shouldWork() throws Exception {
        String createJson = """
                {
                  "title": "Homework task",
                  "description": "Do all parts",
                  "dueDate": "%s",
                  "priority": "HIGH",
                  "tags": ["study", "api"]
                }
                """.formatted(LocalDate.now().plusDays(2));

        MvcResult createRes = mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-API-Version", "2.0.0"))
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        JsonNode createNode = objectMapper.readTree(createRes.getResponse().getContentAsString());
        long id = createNode.get("id").asLong();

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().string("X-API-Version", "2.0.0"));

        String updateJson = """
                {
                  "title": "Updated title",
                  "completed": true
                }
                """;

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void taskCreateValidation_shouldReturn400() throws Exception {
        String invalidJson = """
                {
                  "title": "ab",
                  "description": "x",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.title").exists());
    }

    @Test
    void taskUpdateCustomDueDateValidation_shouldReturn400() throws Exception {
        Task task = new Task();
        task.setTitle("Task");
        task.setDescription("desc");
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDate.now().plusDays(10));
        task.setCreatedAt(LocalDateTime.now().plusDays(5));
        task.setCompleted(false);
        task = taskRepository.save(task);

        String updateJson = """
                {
                  "dueDate": "%s"
                }
                """.formatted(LocalDate.now().plusDays(1));

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType("application/json")
                        .content(updateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.DueDateNotBeforeCreation").exists());
    }

    @Test
    void attachmentEndpoints_shouldSupportUploadDownloadDeleteAndList() throws Exception {
        String createJson = """
                {
                  "title": "Task with file",
                  "description": "desc",
                  "dueDate": "%s",
                  "priority": "LOW",
                  "tags": ["files"]
                }
                """.formatted(LocalDate.now().plusDays(2));

        MvcResult taskRes = mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        long taskId = objectMapper.readTree(taskRes.getResponse().getContentAsString()).get("id").asLong();

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadRes = mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
                        .file((MockMultipartFile) file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andReturn();

        long attachmentId = objectMapper.readTree(uploadRes.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/tasks/{taskId}/attachments", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/attachments/{id}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("test.txt")));

        mockMvc.perform(delete("/api/attachments/{id}", attachmentId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/attachments/{id}", attachmentId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/tasks/{taskId}/attachments", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void attachmentNegative_shouldReturn404ForUnknownTaskOnUpload() throws Exception {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 9999L)
                        .file((MockMultipartFile) file))
                .andExpect(status().isNotFound());
    }

    @Test
    void favoritesEndpoints_shouldWorkWithSession() throws Exception {
        String createJson = """
                {
                  "title": "Fav task",
                  "description": "desc",
                  "dueDate": "%s",
                  "priority": "HIGH",
                  "tags": ["fav"]
                }
                """.formatted(LocalDate.now().plusDays(3));

        MvcResult taskRes = mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        long taskId = objectMapper.readTree(taskRes.getResponse().getContentAsString()).get("id").asLong();
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/{taskId}", taskId).session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(delete("/api/favorites/{taskId}", taskId).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void favoritesAddUnknownTask_shouldReturn404() throws Exception {
        mockMvc.perform(post("/api/favorites/{taskId}", 777L))
                .andExpect(status().isNotFound());
    }

    @Test
    void preferencesEndpoints_shouldUseCookie() throws Exception {
        mockMvc.perform(get("/api/preferences/view"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "detailed"));

        mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("viewPreference", "compact"));
    }

    @Test
    void preferencesInvalidMode_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "unknown"))
                .andExpect(status().isBadRequest());
    }
}
