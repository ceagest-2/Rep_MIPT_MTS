package com.mipt.uriilesnikov.security;

public final class TokenMaskingUtils {

    private TokenMaskingUtils() {
    }

    public static String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "<empty>";
        }
        if (token.length() <= 12) {
            return "******";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}
