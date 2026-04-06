package com.mipt.uriilesnikov.service;

import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.mipt.uriilesnikov.exception.AttachmentNotFoundException;
import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.model.TaskAttachment;
import com.mipt.uriilesnikov.repository.InMemoryTaskAttachmentRepository;
import com.mipt.uriilesnikov.repository.InMemoryTaskRepository;

class AttachmentServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeLoadDeleteAttachment_flow() {
        TaskService taskService = new TaskService(new InMemoryTaskRepository());
        Task task = new Task();
        task.setTitle("Task with attachment");
        task.setDescription("desc");
        task.setPriority(Priority.LOW);
        task.setDueDate(LocalDate.now().plusDays(1));
        task = taskService.createTask(task);

        AttachmentService attachmentService = new AttachmentService(
                new InMemoryTaskAttachmentRepository(),
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
        TaskService taskService = new TaskService(new InMemoryTaskRepository());
        AttachmentService attachmentService = new AttachmentService(
                new InMemoryTaskAttachmentRepository(),
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
