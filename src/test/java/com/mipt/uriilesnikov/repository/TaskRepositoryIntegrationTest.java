package com.mipt.uriilesnikov.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TaskRepositoryIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("todo_test")
            .withUsername("todo_user")
            .withPassword("todo_password");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findTasksDueBetween_shouldReturnOnlyTasksFromRequestedDateRange() {
        LocalDate today = LocalDate.now();

        Task inRangeFirst = buildTask("Due tomorrow", today.plusDays(1));
        Task inRangeSecond = buildTask("Due in five days", today.plusDays(5));
        Task outOfRange = buildTask("Due in twenty days", today.plusDays(20));

        taskRepository.saveAll(List.of(inRangeFirst, inRangeSecond, outOfRange));

        List<Task> found = taskRepository.findTasksDueBetween(today, today.plusDays(7));

        assertEquals(2, found.size());
        assertEquals("Due tomorrow", found.get(0).getTitle());
        assertEquals("Due in five days", found.get(1).getTitle());
        assertTrue(found.stream().allMatch(task -> !task.getDueDate().isAfter(today.plusDays(7))));
    }

    private Task buildTask(String title, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("query check");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(dueDate);
        task.setPriority(Priority.MEDIUM);
        return task;
    }
}
