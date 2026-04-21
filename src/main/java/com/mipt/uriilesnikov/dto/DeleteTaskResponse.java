package com.mipt.uriilesnikov.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeleteTaskResponse(
        boolean success,
        Boolean degraded,
        String message
) {

    public static DeleteTaskResponse ok() {
        return new DeleteTaskResponse(true, null, "Task deleted");
    }

    public static DeleteTaskResponse degraded(String message) {
        return new DeleteTaskResponse(false, true, message);
    }
}
