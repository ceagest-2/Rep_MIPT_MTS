package com.mipt.uriilesnikov.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import com.mipt.uriilesnikov.model.Priority;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task data returned to API clients")
public class TaskResponseDto {
    @Schema(description = "Task ID", example = "1")
    private Long id;

    @Schema(description = "Task title", example = "Prepare project demo")
    private String title;

    @Schema(description = "Task description", example = "Finalize slides")
    private String description;

    @Schema(description = "Task completion status", example = "false")
    private boolean completed;

    @Schema(description = "Creation date/time", example = "2026-04-06T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Task due date", example = "2026-05-10")
    private LocalDate dueDate;

    @Schema(description = "Task priority", example = "HIGH")
    private Priority priority;

    @Schema(description = "Task tags")
    private Set<String> tags = new LinkedHashSet<>();

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
        this.tags = (tags == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(tags);
    }
}
