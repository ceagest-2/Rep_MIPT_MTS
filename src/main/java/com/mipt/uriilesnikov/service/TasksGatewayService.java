package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.client.ExternalTasksClient;
import com.mipt.uriilesnikov.dto.CreatedTaskResult;
import com.mipt.uriilesnikov.dto.DeleteTaskResponse;
import com.mipt.uriilesnikov.dto.TaskDto;
import com.mipt.uriilesnikov.dto.TaskListResponse;
import com.mipt.uriilesnikov.dto.TaskUpsertRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

@Service
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public CreatedTaskResult createTask(TaskUpsertRequest request) {
        return externalTasksClient.createTask(request);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public TaskDto getTask(Long id) {
        return externalTasksClient.getTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "listTasksFallback")
    public TaskListResponse listTasks(Boolean completed, Integer limit) {
        return TaskListResponse.normal(externalTasksClient.listTasks(completed, limit).tasks());
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public DeleteTaskResponse deleteTask(Long id) {
        externalTasksClient.deleteTask(id);
        return DeleteTaskResponse.ok();
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "callUnstableFallback")
    public TaskDto callUnstable(String mode) {
        return externalTasksClient.callUnstableEndpoint(mode);
    }

    public CreatedTaskResult createTaskFallback(TaskUpsertRequest request, Throwable throwable) {
        String message = fallbackMessage("create task", throwable);
        return new CreatedTaskResult(TaskDto.degraded(null, message), null);
    }

    public TaskDto getTaskFallback(Long id, Throwable throwable) {
        return TaskDto.degraded(id, fallbackMessage("read task", throwable));
    }

    public TaskListResponse listTasksFallback(Boolean completed, Integer limit, Throwable throwable) {
        return TaskListResponse.degraded(fallbackMessage("list tasks", throwable));
    }

    public DeleteTaskResponse deleteTaskFallback(Long id, Throwable throwable) {
        return DeleteTaskResponse.degraded(fallbackMessage("delete task", throwable));
    }

    public TaskDto callUnstableFallback(String mode, Throwable throwable) {
        return TaskDto.degraded(-1L, fallbackMessage("call unstable endpoint", throwable));
    }

    private String fallbackMessage(String operation, Throwable throwable) {
        return "Graceful degradation: unable to " + operation + " now (" + throwable.getClass().getSimpleName() + ")";
    }
}
