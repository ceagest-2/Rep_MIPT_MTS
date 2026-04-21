package com.mipt.uriilesnikov.api;

import com.mipt.uriilesnikov.dto.CreatedTaskResult;
import com.mipt.uriilesnikov.dto.DeleteTaskResponse;
import com.mipt.uriilesnikov.dto.TaskDto;
import com.mipt.uriilesnikov.dto.TaskListResponse;
import com.mipt.uriilesnikov.dto.TaskUpsertRequest;
import com.mipt.uriilesnikov.service.TasksGatewayService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TasksController {

    private final TasksGatewayService tasksGatewayService;

    public TasksController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskUpsertRequest request) {
        CreatedTaskResult result = tasksGatewayService.createTask(request);
        URI location = result.location();
        if (location != null) {
            return ResponseEntity.created(location).body(result.task());
        }
        return ResponseEntity.ok(result.task());
    }

    @GetMapping("/{id}")
    public TaskDto getTask(@PathVariable Long id) {
        return tasksGatewayService.getTask(id);
    }

    @GetMapping
    public TaskListResponse listTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit
    ) {
        return tasksGatewayService.listTasks(completed, limit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTaskResponse> deleteTask(@PathVariable Long id) {
        return ResponseEntity.ok(tasksGatewayService.deleteTask(id));
    }

    @GetMapping("/unstable")
    public TaskDto callUnstable(@RequestParam String mode) {
        return tasksGatewayService.callUnstable(mode);
    }
}
