package com.mipt.uriilesnikov.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.mipt.uriilesnikov.dto.TaskCreateDto;
import com.mipt.uriilesnikov.dto.TaskUpdateDto;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TaskMapperTest {
    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void shouldMapCreateDtoToEntity() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("a"));

        Task task = mapper.toEntity(dto);

        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(1, task.getTags().size());
        assertFalse(task.isCompleted());
    }

    @Test
    void shouldPatchOnlyNonNullFields() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Old");
        task.setDescription("OldDesc");
        task.setCreatedAt(LocalDateTime.now());
        task.setCompleted(false);
        task.setPriority(Priority.LOW);

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("New");
        dto.setCompleted(true);

        mapper.updateEntity(dto, task);

        assertEquals("New", task.getTitle());
        assertEquals("OldDesc", task.getDescription());
        assertEquals(Priority.LOW, task.getPriority());
        assertEquals(true, task.isCompleted());
    }
}
