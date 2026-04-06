package com.mipt.uriilesnikov.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

/**
 * Stores and manages favorite task IDs in HTTP session.
 */
@Service
public class FavoritesService {

    public static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

    public Set<Long> getFavoriteTaskIds(HttpSession session) {
        Object value = session.getAttribute(FAVORITES_SESSION_KEY);
        if (value instanceof Set<?> rawSet) {
            Set<Long> result = new LinkedHashSet<>();
            for (Object item : rawSet) {
                if (item instanceof Long longValue) {
                    result.add(longValue);
                }
            }
            return result;
        }
        return new LinkedHashSet<>();
    }

    public Set<Long> addToFavorites(Long taskId, HttpSession session) {
        Set<Long> favoriteIds = getFavoriteTaskIds(session);
        favoriteIds.add(taskId);
        session.setAttribute(FAVORITES_SESSION_KEY, favoriteIds);
        return favoriteIds;
    }

    public Set<Long> removeFromFavorites(Long taskId, HttpSession session) {
        Set<Long> favoriteIds = getFavoriteTaskIds(session);
        favoriteIds.remove(taskId);
        session.setAttribute(FAVORITES_SESSION_KEY, favoriteIds);
        return favoriteIds;
    }
}
