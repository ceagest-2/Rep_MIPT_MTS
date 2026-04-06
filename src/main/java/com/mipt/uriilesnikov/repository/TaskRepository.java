package com.mipt.uriilesnikov.repository;

import java.util.List;
import java.util.Optional;

import com.mipt.uriilesnikov.model.Task;

/**
 * Repository interface for CRUD operations with tasks.
 */
public interface TaskRepository {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    Task save(Task task);
    void deleteById(Long id);
    long count();
}