package com.mipt.uriilesnikov.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User view preference stored in cookie")
public class ViewPreferenceDto {

    @Schema(description = "View mode", example = "compact")
    private String mode;

    public ViewPreferenceDto() {
    }

    public ViewPreferenceDto(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
