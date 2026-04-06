package com.mipt.uriilesnikov.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mipt.uriilesnikov.dto.ErrorResponse;
import com.mipt.uriilesnikov.dto.ViewPreferenceDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/preferences")
@Validated
@Tag(name = "Preferences", description = "Cookie-based user preferences")
public class PreferencesController {

    private static final String VIEW_PREFERENCE_COOKIE = "viewPreference";
    private static final String DEFAULT_VIEW_MODE = "detailed";

    @Operation(summary = "Get current view preference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preference returned")
    })
    @GetMapping("/view")
    public ResponseEntity<ViewPreferenceDto> getViewPreference(
            @CookieValue(value = VIEW_PREFERENCE_COOKIE, required = false) String mode) {

        String resolvedMode = (mode == null || mode.isBlank()) ? DEFAULT_VIEW_MODE : mode;
        ResponseEntity.BodyBuilder response = ResponseEntity.ok();
        if (mode == null || mode.isBlank()) {
            response.header(HttpHeaders.SET_COOKIE, buildCookie(resolvedMode).toString());
        }
        return response.body(new ViewPreferenceDto(resolvedMode));
    }

    @Operation(summary = "Set view preference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preference updated"),
            @ApiResponse(responseCode = "400", description = "Invalid mode",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/view")
    public ResponseEntity<ViewPreferenceDto> setViewPreference(
            @RequestParam @Pattern(regexp = "compact|detailed", message = "mode must be compact or detailed") String mode) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildCookie(mode).toString())
                .body(new ViewPreferenceDto(mode));
    }

    private ResponseCookie buildCookie(String mode) {
        return ResponseCookie.from(VIEW_PREFERENCE_COOKIE, mode)
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(60L * 60L * 24L * 30L)
                .path("/")
                .build();
    }
}
