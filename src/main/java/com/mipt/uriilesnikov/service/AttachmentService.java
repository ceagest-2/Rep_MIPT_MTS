package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.exception.AttachmentNotFoundException;
import com.mipt.uriilesnikov.model.TaskAttachment;
import com.mipt.uriilesnikov.repository.TaskAttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Handles file storage and metadata persistence for task attachments.
 */
@Service
public class AttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private final Path uploadPath;

    public AttachmentService(TaskAttachmentRepository attachmentRepository,
                             TaskService taskService,
                             @Value("${app.upload-dir:uploads}") String uploadDir) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize upload directory: " + uploadPath, e);
        }
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
        taskService.getTaskById(taskId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Attachment file must not be empty");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to prepare upload directory", e);
        }

        Path targetFile = uploadPath.resolve(storedFileName).normalize();
        if (!targetFile.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to store attachment", e);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(originalFileName);
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(file.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());
        return attachmentRepository.save(attachment);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    }

    public List<TaskAttachment> getAttachmentsForTask(Long taskId) {
        taskService.getTaskById(taskId);
        return attachmentRepository.findByTaskId(taskId);
    }

    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadPath.resolve(attachment.getStoredFileName()).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("Attachment file is missing on disk");
            }
            return resource;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read attachment", e);
        }
    }

    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadPath.resolve(attachment.getStoredFileName()).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to delete attachment file", e);
        }
        attachmentRepository.deleteById(attachmentId);
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex);
    }
}
