package com.mipt.uriilesnikov.service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.mipt.uriilesnikov.exception.AttachmentNotFoundException;
import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.model.TaskAttachment;
import com.mipt.uriilesnikov.repository.TaskAttachmentRepository;
import com.mipt.uriilesnikov.repository.TaskRepository;

class AttachmentServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeLoadDeleteAttachment_flow() {
                TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
                TaskAttachmentRepository attachmentRepository = Mockito.mock(TaskAttachmentRepository.class);

                TaskService taskService = new TaskService(taskRepository);
        Task task = new Task();
                task.setId(1L);
        task.setTitle("Task with attachment");
        task.setDescription("desc");
        task.setPriority(Priority.LOW);
        task.setDueDate(LocalDate.now().plusDays(1));

                Mockito.when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

                Map<Long, TaskAttachment> storage = new HashMap<>();
                Mockito.when(attachmentRepository.save(Mockito.any(TaskAttachment.class))).thenAnswer(invocation -> {
                        TaskAttachment toSave = invocation.getArgument(0);
                        if (toSave.getId() == null) {
                                toSave.setId(1L);
                        }
                        storage.put(toSave.getId(), toSave);
                        return toSave;
                });
                Mockito.when(attachmentRepository.findById(Mockito.anyLong())).thenAnswer(invocation ->
                                Optional.ofNullable(storage.get(invocation.getArgument(0)))
                );
                Mockito.doAnswer(invocation -> {
                        storage.remove(invocation.getArgument(0));
                        return null;
                }).when(attachmentRepository).deleteById(Mockito.anyLong());

        AttachmentService attachmentService = new AttachmentService(
                                attachmentRepository,
                taskService,
                tempDir.toString()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes()
        );

        TaskAttachment attachment = attachmentService.storeAttachment(task.getId(), file);
        assertNotNull(attachment.getId());

        Resource resource = attachmentService.loadAsResource(attachment.getId());
        assertTrue(resource.exists());

        attachmentService.deleteAttachment(attachment.getId());
        assertThrows(AttachmentNotFoundException.class, () -> attachmentService.getAttachment(attachment.getId()));
    }

    @Test
    void storeAttachment_shouldFailForUnknownTask() {
        TaskRepository taskRepository = Mockito.mock(TaskRepository.class);
        TaskAttachmentRepository attachmentRepository = Mockito.mock(TaskAttachmentRepository.class);
        Mockito.when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        TaskService taskService = new TaskService(taskRepository);
        AttachmentService attachmentService = new AttachmentService(
                attachmentRepository,
                taskService,
                tempDir.toString()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes()
        );

        assertThrows(TaskNotFoundException.class, () -> attachmentService.storeAttachment(999L, file));
    }
}
