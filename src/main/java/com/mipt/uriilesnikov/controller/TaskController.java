package com.mipt.uriilesnikov.controller;

import com.mipt.uriilesnikov.dto.ErrorResponse;
import com.mipt.uriilesnikov.dto.TaskCreateDto;
import com.mipt.uriilesnikov.dto.TaskResponseDto;
import com.mipt.uriilesnikov.dto.TaskUpdateDto;
import com.mipt.uriilesnikov.mapper.TaskMapper;
import com.mipt.uriilesnikov.model.Task;
import com.mipt.uriilesnikov.service.TaskService;
import com.mipt.uriilesnikov.validation.OnCreate;
import com.mipt.uriilesnikov.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * A REST controller for managing tasks.
 * Implements CRUD endpoints.
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks returned"),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<TaskResponseDto> tasks = taskService.getAllTasks().stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskService.getTotalCount()))
                .body(tasks);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task returned"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskMapper.toResponseDto(task));
    }

    @Operation(summary = "Create task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody @Validated(OnCreate.class) TaskCreateDto taskCreateDto) {
        Task toCreate = taskMapper.toEntity(taskCreateDto);
        Task created = taskService.createTask(toCreate);
        return ResponseEntity
                .created(URI.create("/api/tasks/" + created.getId()))
                .body(taskMapper.toResponseDto(created));
    }

    @Operation(summary = "Update task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
                                                      @RequestBody @Validated(OnUpdate.class) TaskUpdateDto taskUpdateDto) {
        Task existingTask = taskService.getTaskById(id);
        taskMapper.updateEntity(taskUpdateDto, existingTask);
        Task updatedTask = taskService.save(existingTask);
        return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
    }

    @Operation(summary = "Delete task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
