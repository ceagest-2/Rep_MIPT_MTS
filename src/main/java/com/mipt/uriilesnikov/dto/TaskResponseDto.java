package com.mipt.uriilesnikov.dto;

import com.mipt.uriilesnikov.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Task response model")
public class TaskResponseDto {
    @Schema(description = "Task id", example = "1")
    private Long id;

    @Schema(description = "Task title", example = "Finish homework")
    private String title;

    @Schema(description = "Task description", example = "Implement all API endpoints")
    private String description;

    @Schema(description = "Completion status", example = "false")
    private boolean completed;

    @Schema(description = "Creation timestamp", example = "2026-03-21T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Task due date", example = "2026-03-26")
    private LocalDate dueDate;

    @Schema(description = "Task priority", example = "HIGH")
    private Priority priority;

    @Schema(description = "Task tags", example = "[\"study\", \"backend\"]")
    private Set<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
