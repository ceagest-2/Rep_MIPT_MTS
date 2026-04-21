package com.mipt.uriilesnikov.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskUpsertRequest(
        @NotBlank String title,
        String description,
        Boolean completed
) {
}
