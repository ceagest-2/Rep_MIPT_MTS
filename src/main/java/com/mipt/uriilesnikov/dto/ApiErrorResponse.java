package com.mipt.uriilesnikov.dto;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String traceId
) {
}
