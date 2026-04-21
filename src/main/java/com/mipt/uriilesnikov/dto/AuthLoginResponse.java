package com.mipt.uriilesnikov.dto;

import java.time.Instant;

public record AuthLoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt
) {
}
