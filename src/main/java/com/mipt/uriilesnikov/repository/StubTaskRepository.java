package com.mipt.uriilesnikov.repository;

import com.mipt.uriilesnikov.model.Task;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Stub of a repository with fixed data.
 * It is configured as a bean in AppConfig.
 */
public class StubTaskRepository implements TaskRepository {

    @Override
    public List<Task> findAll() {
        return Arrays.asList(
                new Task(1L, "Stub Task 1", "Description 1", false),
                new Task(2L, "Stub Task 2", "Description 2", true)
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
}
