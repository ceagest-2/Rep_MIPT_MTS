package com.mipt.uriilesnikov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mipt.uriilesnikov.model.TaskAttachment;

/**
 * JPA repository for task attachment metadata.
 */
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTask_Id(Long taskId);
}
