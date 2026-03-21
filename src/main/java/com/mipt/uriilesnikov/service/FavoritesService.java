package com.mipt.uriilesnikov.service;

import com.mipt.uriilesnikov.model.Task;
import jakarta.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FavoritesService {
    private static final String FAVORITES_ATTR = "favoriteTaskIds";
    private final TaskService taskService;

    public FavoritesService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void addToFavorites(Long taskId, HttpSession session) {
        taskService.getById(taskId);
        Set<Long> favoriteIds = getFavoriteIdsSet(session);
        favoriteIds.add(taskId);
        session.setAttribute(FAVORITES_ATTR, favoriteIds);
    }

    public void removeFromFavorites(Long taskId, HttpSession session) {
        Set<Long> favoriteIds = getFavoriteIdsSet(session);
        favoriteIds.remove(taskId);
        session.setAttribute(FAVORITES_ATTR, favoriteIds);
    }

    public Set<Long> getFavoriteIds(HttpSession session) {
        return new HashSet<>(getFavoriteIdsSet(session));
    }

    public List<Task> getFavoriteTasks(HttpSession session) {
        return getFavoriteIdsSet(session).stream()
                .map(taskService::getById)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getFavoriteIdsSet(HttpSession session) {
        Object raw = session.getAttribute(FAVORITES_ATTR);
        if (raw instanceof Set<?> set) {
            return (Set<Long>) set;
        }
        Set<Long> favorites = new HashSet<>();
        session.setAttribute(FAVORITES_ATTR, favorites);
        return favorites;
    }
}
