package com.mipt.uriilesnikov.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import com.mipt.uriilesnikov.exception.TasksBulkCompletionException;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;

/**
 * Main task management service backed by JPA repositories.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksWithAttachments() {
        return taskRepository.findAllWithAttachments();
    }

    public List<Task> getTasksByCompletedAndPriority(boolean completed, Priority priority) {
        return taskRepository.findByCompletedAndPriority(completed, priority);
    }

    public List<Task> getTasksDueInNext7Days() {
        LocalDate today = LocalDate.now();
        return taskRepository.findTasksDueBetween(today, today.plusDays(7));
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
        task.setCompleted(false);
        if (task.getTags() == null) {
            task.setTags(new LinkedHashSet<>());
        }
        return save(task);
    }

    public Task save(Task task) {
        if (task.getTags() == null) {
            task.setTags(new LinkedHashSet<>());
        }
        return taskRepository.save(task);
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
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return taskRepository.findAllById(ids);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = TasksBulkCompletionException.class
    )
    public List<Task> bulkCompleteTasks(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Long> uniqueIds = ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<Task> tasks = taskRepository.findAllById(uniqueIds);
        Set<Long> foundIds = tasks.stream().map(Task::getId).collect(Collectors.toSet());
        List<Long> missingIds = uniqueIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new TasksBulkCompletionException(missingIds);
        }

        tasks.forEach(task -> task.setCompleted(true));
        return taskRepository.saveAll(tasks);
    }

    public void deleteTask(Long id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}