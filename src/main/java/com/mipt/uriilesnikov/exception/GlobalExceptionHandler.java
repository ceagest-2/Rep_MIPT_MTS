package com.mipt.uriilesnikov.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.mipt.uriilesnikov.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Centralized API exception handling.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.log-details:true}")
    private boolean logDetails;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Request body validation failed",
                request,
                details
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            details.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Request parameter validation failed",
                request,
                details
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex,
                                                                HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Missing request parameter: " + ex.getParameterName(),
                request,
                Map.of("parameter", ex.getParameterName())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Request body is malformed or unreadable",
                request,
                Map.of("cause", ex.getMostSpecificCause().getMessage())
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex,
                                                         HttpServletRequest request) {
        return buildError(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                request,
                Map.of("method", ex.getHttpMethod(), "url", ex.getRequestURL())
        );
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex,
                                                            HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request, Map.of());
    }

    @ExceptionHandler(AttachmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAttachmentNotFound(AttachmentNotFoundException ex,
                                                                  HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request, Map.of());
    }

    @ExceptionHandler(TasksBulkCompletionException.class)
    public ResponseEntity<ErrorResponse> handleBulkCompletionException(TasksBulkCompletionException ex,
                                                                       HttpServletRequest request) {
        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                Map.of("missingTaskIds", ex.getMissingTaskIds())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                               HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex,
                                                         HttpServletRequest request) {
        String message = logDetails ? ex.getMessage() : "An unexpected error occurred";
        Map<String, Object> details = logDetails
                ? Map.of("exception", ex.getClass().getSimpleName())
                : Map.of();
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, message, request, details);
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status,
                                                     String message,
                                                     HttpServletRequest request,
                                                     Map<String, Object> details) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(Instant.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setPath(request.getRequestURI());
        error.setDetails(details == null ? Map.of() : details);
        return ResponseEntity.status(status).body(error);
    }
}
