package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.exception.TaskNotFoundException;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setCompleted(false);
        return taskRepository.save(task);
    }

    public Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}
