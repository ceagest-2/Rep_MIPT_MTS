package com.mipt.uriilesnikov.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "app.upload-dir=target/test-uploads")
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanupUploads() throws Exception {
        Path uploadPath = Path.of("target/test-uploads");
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                        }
                    });
        }
    }

    @Test
    void uploadDownloadDeleteAttachment_flow() throws Exception {
        long taskId = createTask();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "note.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello attachment".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        JsonNode uploaded = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        long attachmentId = uploaded.get("id").asLong();

        mockMvc.perform(get("/api/tasks/{taskId}/attachments", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("note.txt")))
                .andExpect(header().string("X-API-Version", "2.0.0"));

        mockMvc.perform(delete("/api/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadAttachment_shouldFailForUnknownTask() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "note.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", 999999)
                        .file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadAttachment_shouldFailForEmptyFile() throws Exception {
        long taskId = createTask();
        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/tasks/{taskId}/attachments", taskId)
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    private long createTask() throws Exception {
        String payload = """
                {
                  "title": "Attachment task",
                  "description": "Task with files",
                  "dueDate": "%s",
                  "priority": "MEDIUM",
                  "tags": ["files"]
                }
                """.formatted(LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
