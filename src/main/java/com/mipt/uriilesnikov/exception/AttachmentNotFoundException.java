package com.mipt.uriilesnikov.exception;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(Long id) {
        super("Attachment with id " + id + " not found");
    }
}
