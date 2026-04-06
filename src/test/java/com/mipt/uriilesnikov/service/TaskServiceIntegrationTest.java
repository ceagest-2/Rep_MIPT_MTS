package com.mipt.uriilesnikov.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mipt.uriilesnikov.exception.TasksBulkCompletionException;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    void bulkCompleteTasks_shouldRollbackWhenAnyTaskIdMissing() {
        Task first = taskService.createTask(buildTask("first"));
        Task second = taskService.createTask(buildTask("second"));

        assertThrows(
                TasksBulkCompletionException.class,
                () -> taskService.bulkCompleteTasks(List.of(first.getId(), 999999L, second.getId()))
        );

        Task firstFromDb = taskRepository.findById(first.getId()).orElseThrow();
        Task secondFromDb = taskRepository.findById(second.getId()).orElseThrow();

        assertFalse(firstFromDb.isCompleted());
        assertFalse(secondFromDb.isCompleted());
    }

    @Test
    void bulkCompleteTasks_shouldMarkAllTasksCompletedWhenIdsExist() {
        Task first = taskService.createTask(buildTask("first"));
        Task second = taskService.createTask(buildTask("second"));

        taskService.bulkCompleteTasks(List.of(first.getId(), second.getId()));

        Task firstFromDb = taskRepository.findById(first.getId()).orElseThrow();
        Task secondFromDb = taskRepository.findById(second.getId()).orElseThrow();

        assertTrue(firstFromDb.isCompleted());
        assertTrue(secondFromDb.isCompleted());
    }

    private Task buildTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("desc");
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDate.now().plusDays(1));
        return task;
    }
}
