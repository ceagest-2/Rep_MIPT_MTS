package com.mipt.uriilesnikov.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.Hibernate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.model.TaskAttachment;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryDataJpaTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompletedAndPriority_shouldReturnMatchingTasks() {
        Task low = buildTask("Low", Priority.LOW, false, LocalDate.now().plusDays(1));
        Task highCompleted = buildTask("High completed", Priority.HIGH, true, LocalDate.now().plusDays(2));
        Task highNotCompleted = buildTask("High open", Priority.HIGH, false, LocalDate.now().plusDays(3));

        taskRepository.saveAll(List.of(low, highCompleted, highNotCompleted));

        List<Task> found = taskRepository.findByCompletedAndPriority(true, Priority.HIGH);

        assertEquals(1, found.size());
        assertEquals("High completed", found.get(0).getTitle());
    }

    @Test
    void findTasksDueBetween_shouldReturnOnlyRangeMatches() {
        LocalDate today = LocalDate.now();
        Task inRange = buildTask("Due soon", Priority.MEDIUM, false, today.plusDays(5));
        Task outOfRange = buildTask("Due later", Priority.MEDIUM, false, today.plusDays(20));

        taskRepository.saveAll(List.of(inRange, outOfRange));

        List<Task> found = taskRepository.findTasksDueBetween(today, today.plusDays(7));

        assertEquals(1, found.size());
        assertEquals("Due soon", found.get(0).getTitle());
    }

    @Test
    void findAllWithAttachments_shouldLoadAttachmentsWithoutLazyInitError() {
        Task task = taskRepository.save(buildTask("With file", Priority.HIGH, false, LocalDate.now().plusDays(1)));

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("doc.txt");
        attachment.setStoredFileName("stored-doc.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(42L);
        attachment.setUploadedAt(LocalDateTime.now());
        taskAttachmentRepository.save(attachment);

        entityManager.flush();
        entityManager.clear();

        Task loaded = taskRepository.findAllWithAttachments().stream()
                .filter(t -> t.getId().equals(task.getId()))
                .findFirst()
                .orElseThrow();

        assertTrue(Hibernate.isInitialized(loaded.getAttachments()));
        assertEquals(1, loaded.getAttachments().size());
    }

    private Task buildTask(String title, Priority priority, boolean completed, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("desc");
        task.setPriority(priority);
        task.setCompleted(completed);
        task.setDueDate(dueDate);
        task.setCreatedAt(LocalDateTime.now());
        task.setTags(new LinkedHashSet<>(List.of("test")));
        return task;
    }
}
