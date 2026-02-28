package com.mipt.uriilesnikov.scope;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bean with a scope request.
 * Stores the request ID and the start time.
 */
public class RequestScopedBean {
    private final String requestId;
    private final LocalDateTime startTime;

    public RequestScopedBean() {
        this.requestId = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
