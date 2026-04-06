package com.mipt.uriilesnikov.dto;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API error response")
public class ErrorResponse {

    @Schema(description = "Error timestamp")
    private Instant timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP error reason", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed message")
    private String message;

    @Schema(description = "Request path", example = "/api/tasks")
    private String path;

    @Schema(description = "Additional details map")
    private Map<String, Object> details = new LinkedHashMap<>();

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
