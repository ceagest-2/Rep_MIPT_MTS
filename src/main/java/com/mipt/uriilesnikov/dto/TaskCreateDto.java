package com.mipt.uriilesnikov.dto;

import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO for creating a task")
public class TaskCreateDto {
    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 100, groups = OnCreate.class)
    @Schema(description = "Task title", example = "Finish homework", minLength = 3, maxLength = 100)
    private String title;

    @Size(max = 500, groups = OnCreate.class)
    @Schema(description = "Task description", example = "Implement all API endpoints", maxLength = 500)
    private String description;

    @FutureOrPresent(groups = OnCreate.class)
    @Schema(description = "Task due date", example = "2026-03-25")
    private LocalDate dueDate;

    @NotNull(groups = OnCreate.class)
    @Schema(description = "Task priority", example = "HIGH")
    private Priority priority;

    @Size(max = 5, groups = OnCreate.class)
    @Schema(description = "Task tags", example = "[\"study\", \"backend\"]")
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
