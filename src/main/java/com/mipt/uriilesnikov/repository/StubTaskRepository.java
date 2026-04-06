package com.mipt.uriilesnikov.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;

/**
 * Stub of a repository with fixed data.
 * It is configured as a bean in AppConfig.
 */
public class StubTaskRepository implements TaskRepository {

    @Override
    public List<Task> findAll() {
        return Arrays.asList(
            new Task(
                1L,
                "Stub Task 1",
                "Description 1",
                false,
                LocalDateTime.now().minusDays(2),
                LocalDate.now().plusDays(3),
                Priority.MEDIUM,
                new LinkedHashSet<>(List.of("stub", "demo"))
            ),
            new Task(
                2L,
                "Stub Task 2",
                "Description 2",
                true,
                LocalDateTime.now().minusDays(5),
                LocalDate.now().plusDays(1),
                Priority.HIGH,
                new LinkedHashSet<>(List.of("stub"))
            )
        );
    }

    @Override
    public Optional<Task> findById(Long id) {
        return findAll().stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public Task save(Task task) {
        // В заглушке сохранение не меняет состояние
        return task;
    }

    @Override
    public void deleteById(Long id) {
        // В заглушке удаление не меняет состояние
    }

    @Override
    public long count() {
        return findAll().size();
    }
}
