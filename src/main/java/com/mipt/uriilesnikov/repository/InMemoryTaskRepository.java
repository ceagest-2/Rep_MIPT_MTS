package com.mipt.uriilesnikov.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.mipt.uriilesnikov.model.Task;

/**
 * The main repository that stores data in memory.
 * Marked as @Primary for default use.
 */
@Primary
@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(currentId.getAndIncrement());
        }
        storage.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public long count() {
        return storage.size();
    }
}
