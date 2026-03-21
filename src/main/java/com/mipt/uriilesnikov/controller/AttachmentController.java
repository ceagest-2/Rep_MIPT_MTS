package com.mipt.uriilesnikov.controller;

import com.mipt.uriilesnikov.dto.AttachmentResponseDto;
import com.mipt.uriilesnikov.model.TaskAttachment;
import com.mipt.uriilesnikov.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Upload attachment for task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attachment uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDto> upload(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
        TaskAttachment attachment = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.status(201).body(toDto(attachment));
    }

    @Operation(summary = "Download attachment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded"),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId) {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);

        String contentType = attachment.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : attachment.getContentType();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @Operation(summary = "Delete attachment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Attachment deleted"),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List task attachments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attachments fetched"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> getByTask(@PathVariable Long taskId) {
        List<AttachmentResponseDto> response = attachmentService.getByTaskId(taskId).stream().map(this::toDto).toList();
        return ResponseEntity.ok(response);
    }

    private AttachmentResponseDto toDto(TaskAttachment attachment) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());
        return dto;
    }
}
