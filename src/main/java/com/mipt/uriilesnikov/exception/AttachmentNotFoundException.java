package com.mipt.uriilesnikov.exception;

/**
 * Thrown when attachment with specified ID is not found.
 */
public class AttachmentNotFoundException extends RuntimeException {

    public AttachmentNotFoundException(Long attachmentId) {
        super("Attachment with id " + attachmentId + " was not found");
    }
}
