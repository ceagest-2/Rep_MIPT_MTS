package com.mipt.uriilesnikov.repository;

import java.util.List;
import java.util.Optional;

import com.mipt.uriilesnikov.model.TaskAttachment;

/**
 * Repository for task attachment metadata.
 */
public interface TaskAttachmentRepository {
    TaskAttachment save(TaskAttachment attachment);
    Optional<TaskAttachment> findById(Long id);
    List<TaskAttachment> findByTaskId(Long taskId);
    void deleteById(Long id);
}
