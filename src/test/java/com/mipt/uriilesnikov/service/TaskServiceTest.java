package com.mipt.uriilesnikov.service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.InMemoryTaskRepository;

class TaskServiceTest {

    @Test
    void createTask_shouldPopulateMetadataAndCount() {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        Task task = new Task();
        task.setTitle("Task service test");
        task.setDescription("desc");
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDate.now().plusDays(2));

        Task created = service.createTask(task);

        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertEquals(1L, service.getTotalCount());
    }

    @Test
    void getTaskById_shouldThrowWhenMissing() {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        assertThrows(TaskNotFoundException.class, () -> service.getTaskById(123L));
    }
}
