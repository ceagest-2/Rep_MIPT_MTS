package com.mipt.uriilesnikov.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;

import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mipt.uriilesnikov.dto.TaskCreateDto;
import com.mipt.uriilesnikov.dto.TaskResponseDto;
import com.mipt.uriilesnikov.mapper.TaskMapper;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.service.TaskService;
import com.mipt.uriilesnikov.testsupport.MockitoBean;

@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
        @MockBean
    private TaskService taskService;

    @MockitoBean
        @MockBean
    private TaskMapper taskMapper;

    @Test
    void postApiTasks_shouldCreateTaskAndReturnCreatedResponse() throws Exception {
        Task entityToCreate = new Task();
        entityToCreate.setTitle("Prepare report");
        entityToCreate.setDescription("Quarterly report");
        entityToCreate.setPriority(Priority.HIGH);
        entityToCreate.setDueDate(LocalDate.now().plusDays(2));
        entityToCreate.setTags(new LinkedHashSet<>());

        Task createdTask = new Task();
        createdTask.setId(101L);
        createdTask.setTitle("Prepare report");
        createdTask.setDescription("Quarterly report");
        createdTask.setCompleted(false);
        createdTask.setCreatedAt(LocalDateTime.now());
        createdTask.setPriority(Priority.HIGH);
        createdTask.setDueDate(entityToCreate.getDueDate());
        createdTask.setTags(new LinkedHashSet<>());

        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setId(101L);
        responseDto.setTitle("Prepare report");
        responseDto.setDescription("Quarterly report");
        responseDto.setCompleted(false);
        responseDto.setCreatedAt(createdTask.getCreatedAt());
        responseDto.setPriority(Priority.HIGH);
        responseDto.setDueDate(entityToCreate.getDueDate());
        responseDto.setTags(new LinkedHashSet<>());

        when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(entityToCreate);
        when(taskService.createTask(entityToCreate)).thenReturn(createdTask);
        when(taskMapper.toResponseDto(createdTask)).thenReturn(responseDto);

        String request = """
                {
                  "title": "Prepare report",
                  "description": "Quarterly report",
                  "dueDate": "%s",
                  "priority": "HIGH",
                  "tags": ["work", "report"]
                }
                """.formatted(LocalDate.now().plusDays(2));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/101"))
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.title").value("Prepare report"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void getApiTasksById_shouldReturnExistingTask() throws Exception {
        Task task = new Task();
        task.setId(7L);
        task.setTitle("Stored task");
        task.setDescription("Stored description");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(LocalDate.now().plusDays(5));
        task.setPriority(Priority.MEDIUM);
        task.setTags(new LinkedHashSet<>());

        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setId(7L);
        responseDto.setTitle("Stored task");
        responseDto.setDescription("Stored description");
        responseDto.setCompleted(false);
        responseDto.setCreatedAt(task.getCreatedAt());
        responseDto.setDueDate(task.getDueDate());
        responseDto.setPriority(Priority.MEDIUM);
        responseDto.setTags(new LinkedHashSet<>());

        when(taskService.getTaskById(7L)).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(responseDto);

        mockMvc.perform(get("/api/tasks/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.title").value("Stored task"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void postApiTasks_shouldReturnBadRequestForInvalidBody() throws Exception {
        String invalidRequest = """
                {
                  "title": "ab",
                  "priority": null
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.title", notNullValue()))
                .andExpect(jsonPath("$.details.priority", notNullValue()));

        verifyNoInteractions(taskService);
    }
}
