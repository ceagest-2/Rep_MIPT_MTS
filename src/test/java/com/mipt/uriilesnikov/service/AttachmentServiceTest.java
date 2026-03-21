package com.mipt.uriilesnikov.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mipt.uriilesnikov.exception.AttachmentNotFoundException;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "app.upload-dir=target/test-uploads-service"
})
class AttachmentServiceTest {
    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private TaskService taskService;

    @Test
    void storeLoadDelete_shouldWork() {
        Task task = new Task();
        task.setTitle("Task");
        task.setDescription("desc");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(Priority.HIGH);
        Task savedTask = taskService.create(task);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.txt",
                "text/plain",
                "abc".getBytes(StandardCharsets.UTF_8)
        );

        var attachment = attachmentService.storeAttachment(savedTask.getId(), file);
        assertEquals("doc.txt", attachment.getFileName());

        var loaded = attachmentService.loadAsResource(attachment.getId());
        assertEquals(true, loaded.exists());

        attachmentService.deleteAttachment(attachment.getId());
        assertThrows(AttachmentNotFoundException.class, () -> attachmentService.getAttachment(attachment.getId()));
    }

    @Test
    void storeEmptyFile_shouldFail() {
        Task task = new Task();
        task.setTitle("Task");
        task.setDescription("desc");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(Priority.HIGH);
        Task savedTask = taskService.create(task);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> attachmentService.storeAttachment(savedTask.getId(), file));
    }
}
