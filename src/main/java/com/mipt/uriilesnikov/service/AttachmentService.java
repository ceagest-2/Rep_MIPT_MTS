package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.exception.AttachmentNotFoundException;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.model.TaskAttachment;
import com.mipt.uriilesnikov.repository.TaskAttachmentRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentService {
    private final TaskAttachmentRepository repository;
    private final TaskService taskService;
    private final Path uploadDir;

    public AttachmentService(
            TaskAttachmentRepository repository,
            TaskService taskService,
            @Value("${app.upload-dir:uploads}") String uploadDir
    ) throws IOException {
        this.repository = repository;
        this.taskService = taskService;
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
        Task task = taskService.getById(taskId);
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String originalName = file.getOriginalFilename();
        String safeName = originalName == null || originalName.isBlank() ? "file" : Path.of(originalName).getFileName().toString();
        String storedName = UUID.randomUUID() + "_" + safeName;
        Path target = uploadDir.resolve(storedName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot store file", e);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(task.getId());
        attachment.setFileName(safeName);
        attachment.setStoredFileName(storedName);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());
        return repository.save(attachment);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return repository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    }

    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path path = uploadDir.resolve(attachment.getStoredFileName()).normalize();
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new AttachmentNotFoundException(attachmentId);
            }
            return resource;
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read file", e);
        }
    }

    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path path = uploadDir.resolve(attachment.getStoredFileName()).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete file", e);
        }
        repository.deleteById(attachmentId);
    }

    public List<TaskAttachment> getByTaskId(Long taskId) {
        taskService.getById(taskId);
        return repository.findByTaskId(taskId);
    }
}
