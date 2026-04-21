package com.mipt.uriilesnikov.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDto(
        Long id,
        String title,
        String description,
        Boolean completed,
        Boolean degraded,
        String message
) {

    public static TaskDto normal(Long id, String title, String description, Boolean completed) {
        return new TaskDto(id, title, description, completed, null, null);
    }

    public static TaskDto degraded(Long id, String message) {
        return new TaskDto(id, "unavailable", "External API is unavailable", false, true, message);
    }
}
