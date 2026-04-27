package com.mipt.uriilesnikov.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.model.TaskAttachment;

@DataJpaTest
@ActiveProfiles("test")
class TaskAttachmentRepositoryDataJpaTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Test
    void findByTaskId_shouldReturnOnlyAttachmentsForRequestedTask() {
        Task firstTask = taskRepository.save(buildTask("Task 1"));
        Task secondTask = taskRepository.save(buildTask("Task 2"));

        taskAttachmentRepository.save(buildAttachment(firstTask, "first.txt"));
        taskAttachmentRepository.save(buildAttachment(secondTask, "second.txt"));

        List<TaskAttachment> found = taskAttachmentRepository.findByTask_Id(firstTask.getId());

        assertEquals(1, found.size());
        assertEquals("first.txt", found.get(0).getFileName());
    }

    private Task buildTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("desc");
        task.setPriority(Priority.LOW);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    private TaskAttachment buildAttachment(Task task, String fileName) {
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName(fileName);
        attachment.setStoredFileName(fileName + "-stored");
        attachment.setContentType("text/plain");
        attachment.setSize(10L);
        attachment.setUploadedAt(LocalDateTime.now());
        return attachment;
    }
}
