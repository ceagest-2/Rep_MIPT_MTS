package com.mipt.uriilesnikov.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;
import com.mipt.uriilesnikov.testsupport.MockitoBean;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private HttpServletRequest request;

    @Test
    void givenExistingTask_whenUpdateStatus_thenTaskIsSavedWithUpdatedCompletionFlag() {
        Long taskId = 42L;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Task existing = new Task();
        existing.setId(taskId);
        existing.setTitle("Initial title");
        existing.setDescription("Initial description");
        existing.setCompleted(false);
        existing.setCreatedAt(createdAt);
        existing.setDueDate(LocalDate.now().plusDays(3));
        existing.setPriority(Priority.MEDIUM);
        existing.setTags(new LinkedHashSet<>());

        Task taskDetails = new Task();
        taskDetails.setTitle("Initial title");
        taskDetails.setDescription("Initial description");
        taskDetails.setCompleted(true);
        taskDetails.setDueDate(LocalDate.now().plusDays(3));
        taskDetails.setPriority(Priority.MEDIUM);
        taskDetails.setTags(new LinkedHashSet<>());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setLastModifiedAt(LocalDateTime.now());
            return saved;
        });

        Task updated = taskService.updateTask(taskId, taskDetails);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        assertEquals(taskId, savedTask.getId());
        assertEquals(createdAt, savedTask.getCreatedAt());
        assertEquals(true, savedTask.isCompleted());
        assertEquals(taskId, updated.getId());
        assertEquals(true, updated.isCompleted());
    }
}
