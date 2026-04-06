package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The main task management service.
 * Demonstrates lifecycle (@PostConstruct, @PreDestroy) and @Value injection.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final Map<Long, Task> taskCache = new HashMap<>();

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Initializing the cache at application startup.
     */
    @PostConstruct
    public void init() {
        System.out.println(" [TaskService] Initializing cache for app: " + appName + " v" + appVersion);
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            taskCache.put(task.getId(), task);
        }
    }

    /**
     * Cleaning up resources before destroying the bean.
     */
    @PreDestroy
    public void cleanup() {
        System.out.println(" [TaskService] Cleaning up. Tasks in cache: " + taskCache.size());
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public long getTotalCount() {
        return taskRepository.count();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task createTask(Task task) {
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        if (task.getTags() == null) {
            task.setTags(new LinkedHashSet<>());
        }
        return save(task);
    }

    public Task save(Task task) {
        Task saved = taskRepository.save(task);
        taskCache.put(saved.getId(), saved);
        return saved;
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task existing = getTaskById(id);
        taskDetails.setId(existing.getId());
        taskDetails.setCreatedAt(existing.getCreatedAt());
        if (taskDetails.getTags() == null) {
            taskDetails.setTags(existing.getTags());
        }
        return save(taskDetails);
    }

    public List<Task> getTasksByIds(Set<Long> ids) {
        List<Task> tasks = new ArrayList<>();
        for (Long id : ids) {
            taskRepository.findById(id).ifPresent(tasks::add);
        }
        return tasks;
    }

    public void deleteTask(Long id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        taskCache.remove(id);
    }
}