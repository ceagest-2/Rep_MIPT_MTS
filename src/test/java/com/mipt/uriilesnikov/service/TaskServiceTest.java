package com.mipt.uriilesnikov.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService service;

    @Test
    void createTask_shouldPopulateMetadataAndCount() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task toSave = invocation.getArgument(0);
            if (toSave.getId() == null) {
                toSave.setId(1L);
            }
            return toSave;
        });
        when(taskRepository.count()).thenReturn(1L);

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
        when(taskRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> service.getTaskById(123L));
    }
}
