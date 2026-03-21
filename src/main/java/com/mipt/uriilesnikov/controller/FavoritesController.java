package com.mipt.uriilesnikov.controller;

import com.mipt.uriilesnikov.dto.TaskResponseDto;
import com.mipt.uriilesnikov.mapper.TaskMapper;
import com.mipt.uriilesnikov.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {
    private final FavoritesService favoritesService;
    private final TaskMapper taskMapper;

    public FavoritesController(FavoritesService favoritesService, TaskMapper taskMapper) {
        this.favoritesService = favoritesService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Add task to favorites")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Added to favorites"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> add(@PathVariable Long taskId, HttpSession session) {
        favoritesService.addToFavorites(taskId, session);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove task from favorites")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removed from favorites")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> remove(@PathVariable Long taskId, HttpSession session) {
        favoritesService.removeFromFavorites(taskId, session);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get favorite tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favorites fetched")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<TaskResponseDto> response = favoritesService.getFavoriteTasks(session)
                .stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }
}
