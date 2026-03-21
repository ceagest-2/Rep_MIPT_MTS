package com.mipt.uriilesnikov.controller;

import com.mipt.uriilesnikov.dto.TaskCreateDto;
import com.mipt.uriilesnikov.dto.TaskResponseDto;
import com.mipt.uriilesnikov.dto.TaskUpdateDto;
import com.mipt.uriilesnikov.mapper.TaskMapper;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.service.TaskService;
import com.mipt.uriilesnikov.validation.OnCreate;
import com.mipt.uriilesnikov.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks fetched")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAll() {
        List<TaskResponseDto> response = taskService.getAll().stream().map(taskMapper::toResponseDto).toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(response.size()))
                .body(response);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task fetched"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskMapper.toResponseDto(taskService.getById(id)));
    }

    @Operation(summary = "Create task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDto> create(@Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
        Task created = taskService.create(taskMapper.toEntity(dto));
        return ResponseEntity.status(201).body(taskMapper.toResponseDto(created));
    }

    @Operation(summary = "Update task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> update(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto
    ) {
        Task existing = taskService.getById(id);
        taskMapper.updateEntity(dto, existing);
        Task saved = taskService.save(existing);
        return ResponseEntity.ok(taskMapper.toResponseDto(saved));
    }

    @Operation(summary = "Delete task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
