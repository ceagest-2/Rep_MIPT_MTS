package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = getTaskById(id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}