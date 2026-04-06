package com.mipt.uriilesnikov.service;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

class FavoritesServiceTest {

    private final FavoritesService favoritesService = new FavoritesService();

    @Test
    void addAndRemoveFavorites_shouldUseSessionState() {
        MockHttpSession session = new MockHttpSession();

        Set<Long> afterAdd = favoritesService.addToFavorites(10L, session);
        assertEquals(Set.of(10L), afterAdd);

        Set<Long> afterRemove = favoritesService.removeFromFavorites(10L, session);
        assertTrue(afterRemove.isEmpty());
    }

    @Test
    void getFavorites_shouldReturnEmptySetByDefault() {
        Set<Long> ids = favoritesService.getFavoriteTaskIds(new MockHttpSession());
        assertTrue(ids.isEmpty());
    }
}
