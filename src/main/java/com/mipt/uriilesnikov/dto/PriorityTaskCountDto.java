package com.mipt.uriilesnikov.dto;

import com.mipt.uriilesnikov.model.Priority;

/**
 * Aggregated task statistics grouped by priority.
 */
public class PriorityTaskCountDto {

    private Priority priority;
    private long count;

    public PriorityTaskCountDto() {
    }

    public PriorityTaskCountDto(Priority priority, long count) {
        this.priority = priority;
        this.count = count;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
