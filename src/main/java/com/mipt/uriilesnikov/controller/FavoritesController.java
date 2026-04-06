package com.mipt.uriilesnikov.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mipt.uriilesnikov.dto.ErrorResponse;
import com.mipt.uriilesnikov.dto.TaskResponseDto;
import com.mipt.uriilesnikov.mapper.TaskMapper;
import com.mipt.uriilesnikov.service.FavoritesService;
import com.mipt.uriilesnikov.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Session-backed favorite tasks")
public class FavoritesController {

    private final FavoritesService favoritesService;
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public FavoritesController(FavoritesService favoritesService, TaskService taskService, TaskMapper taskMapper) {
        this.favoritesService = favoritesService;
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Add task to favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task added"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{taskId}")
    public ResponseEntity<List<Long>> addFavorite(@PathVariable Long taskId, HttpSession session) {
        taskService.getTaskById(taskId);
        Set<Long> ids = favoritesService.addToFavorites(taskId, session);
        return ResponseEntity.ok(new ArrayList<>(ids));
    }

    @Operation(summary = "Remove task from favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task removed")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<List<Long>> removeFavorite(@PathVariable Long taskId, HttpSession session) {
        Set<Long> ids = favoritesService.removeFromFavorites(taskId, session);
        return ResponseEntity.ok(new ArrayList<>(ids));
    }

    @Operation(summary = "Get favorite tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorites returned")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        Set<Long> ids = favoritesService.getFavoriteTaskIds(session);
        List<TaskResponseDto> favorites = taskService.getTasksByIds(ids)
                .stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(favorites);
    }
}
