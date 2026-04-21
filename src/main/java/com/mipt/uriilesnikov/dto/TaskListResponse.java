package com.mipt.uriilesnikov.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskListResponse(
        List<TaskDto> tasks,
        Boolean degraded,
        String message
) {

    public static TaskListResponse normal(List<TaskDto> tasks) {
        return new TaskListResponse(tasks, null, null);
    }

    public static TaskListResponse degraded(String message) {
        return new TaskListResponse(List.of(), true, message);
    }
}
