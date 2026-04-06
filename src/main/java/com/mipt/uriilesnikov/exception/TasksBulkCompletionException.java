package com.mipt.uriilesnikov.exception;

import java.util.List;

/**
 * Thrown when one or more task IDs are missing during bulk completion.
 */
public class TasksBulkCompletionException extends RuntimeException {

    private final List<Long> missingTaskIds;

    public TasksBulkCompletionException(List<Long> missingTaskIds) {
        super("Bulk completion failed. Missing task IDs: " + missingTaskIds);
        this.missingTaskIds = List.copyOf(missingTaskIds);
    }

    public List<Long> getMissingTaskIds() {
        return missingTaskIds;
    }
}
