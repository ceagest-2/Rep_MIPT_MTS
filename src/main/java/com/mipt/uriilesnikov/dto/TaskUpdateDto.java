package com.mipt.uriilesnikov.dto;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.validation.DueDateNotBeforeCreation;
import com.mipt.uriilesnikov.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@DueDateNotBeforeCreation(groups = OnUpdate.class)
@Schema(description = "DTO for updating a task")
public class TaskUpdateDto {
    @Size(min = 3, max = 100, groups = OnUpdate.class)
    @Schema(description = "Task title", example = "Finish homework", minLength = 3, maxLength = 100)
    private String title;

    @Size(max = 500, groups = OnUpdate.class)
    @Schema(description = "Task description", example = "Implement all API endpoints", maxLength = 500)
    private String description;

    @Schema(description = "Task completion flag", example = "false")
    private Boolean completed;

    @FutureOrPresent(groups = OnUpdate.class)
    @Schema(description = "Task due date", example = "2026-03-26")
    private LocalDate dueDate;

    @Schema(description = "Task priority", example = "MEDIUM")
    private Priority priority;

    @Size(max = 5, groups = OnUpdate.class)
    @Schema(description = "Task tags", example = "[\"study\", \"urgent\"]")
    private Set<String> tags;

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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
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
