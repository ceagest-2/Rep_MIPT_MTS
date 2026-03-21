package com.mipt.uriilesnikov.repository;

import com.mipt.uriilesnikov.model.TaskAttachment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {
    private final ConcurrentHashMap<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(idGenerator.incrementAndGet());
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
        return storage.values().stream()
                .filter(a -> a.getTaskId().equals(taskId))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
