package com.mipt.uriilesnikov.exception;

/**
 * Thrown when task with specified ID is not found.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long taskId) {
        super("Task with id " + taskId + " was not found");
    }
}
