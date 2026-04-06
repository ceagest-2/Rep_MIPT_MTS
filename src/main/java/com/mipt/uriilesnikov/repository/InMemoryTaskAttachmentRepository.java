package com.mipt.uriilesnikov.repository;

import com.mipt.uriilesnikov.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory metadata repository for task attachments.
 */
@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

    private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(currentId.getAndIncrement());
        }
        storage.put(attachment.getId(), attachment);
        return attachment;
    }

    @Override
    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TaskAttachment> findByTaskId(Long taskId) {
        List<TaskAttachment> result = new ArrayList<>();
        for (TaskAttachment attachment : storage.values()) {
            if (attachment.getTaskId().equals(taskId)) {
                result.add(attachment);
            }
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
