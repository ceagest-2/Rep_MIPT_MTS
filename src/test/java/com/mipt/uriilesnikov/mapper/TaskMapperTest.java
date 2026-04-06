package com.mipt.uriilesnikov.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.mipt.uriilesnikov.dto.TaskCreateDto;
import com.mipt.uriilesnikov.dto.TaskUpdateDto;
import com.mipt.uriilesnikov.model.Priority;
import com.mipt.uriilesnikov.model.Task;

class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void toEntity_shouldMapCreateDto() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Mapper task");
        dto.setDescription("Mapper description");
        dto.setDueDate(LocalDate.now().plusDays(3));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("a", "b"));

        Task entity = mapper.toEntity(dto);

        assertNull(entity.getId());
        assertEquals("Mapper task", entity.getTitle());
        assertEquals("Mapper description", entity.getDescription());
        assertEquals(dto.getDueDate(), entity.getDueDate());
        assertEquals(Priority.HIGH, entity.getPriority());
        assertFalse(entity.isCompleted());
        assertNotNull(entity.getTags());
        assertEquals(2, entity.getTags().size());
    }

    @Test
    void updateEntity_shouldIgnoreNullFields() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setTitle("Old title");
        existing.setDescription("Old description");
        existing.setCompleted(false);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setPriority(Priority.LOW);
        existing.setTags(new LinkedHashSet<>(Set.of("old")));

        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("New title");
        updateDto.setCompleted(true);

        mapper.updateEntity(updateDto, existing);

        assertEquals("New title", existing.getTitle());
        assertEquals("Old description", existing.getDescription());
        assertEquals(Priority.LOW, existing.getPriority());
        assertEquals(Set.of("old"), existing.getTags());
        assertEquals(true, existing.isCompleted());
    }
}
