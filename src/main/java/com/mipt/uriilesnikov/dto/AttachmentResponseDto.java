package com.mipt.uriilesnikov.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Metadata for a task attachment")
public class AttachmentResponseDto {

    @Schema(description = "Attachment ID", example = "10")
    private Long id;

    @Schema(description = "Original file name", example = "requirements.pdf")
    private String fileName;

    @Schema(description = "File size in bytes", example = "10240")
    private long size;

    @Schema(description = "Upload date/time", example = "2026-04-06T15:25:00")
    private LocalDateTime uploadedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
