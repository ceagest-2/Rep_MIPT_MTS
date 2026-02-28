package com.mipt.uriilesnikov.repository;

import com.mipt.uriilesnikov.model.Task;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CRUD operations with tasks.
 */
public interface TaskRepository {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    Task save(Task task);
    void deleteById(Long id);
}