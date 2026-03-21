package com.mipt.uriilesnikov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {
    private static final String COOKIE_NAME = "viewPreference";

    @Operation(summary = "Get view preference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preference returned")
    })
    @GetMapping("/view")
    public ResponseEntity<String> getViewPreference(
            @CookieValue(name = COOKIE_NAME, required = false) String mode,
            HttpServletResponse response
    ) {
        if (mode == null || mode.isBlank()) {
            setPreferenceCookie(response, "detailed");
            return ResponseEntity.ok("detailed");
        }
        return ResponseEntity.ok(mode);
    }

    @Operation(summary = "Set view preference")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preference updated"),
            @ApiResponse(responseCode = "400", description = "Invalid mode")
    })
    @PostMapping("/view")
    public ResponseEntity<String> setViewPreference(@RequestParam String mode, HttpServletResponse response) {
        if (!"compact".equals(mode) && !"detailed".equals(mode)) {
            throw new IllegalArgumentException("Mode must be compact or detailed");
        }
        setPreferenceCookie(response, mode);
        return ResponseEntity.ok(mode);
    }

    private void setPreferenceCookie(HttpServletResponse response, String mode) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, mode)
                .path("/")
                .httpOnly(false)
                .maxAge(60L * 60L * 24L * 30L)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
