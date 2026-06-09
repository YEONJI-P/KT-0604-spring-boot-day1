package com.sesac.aibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatLogRequest(
        @NotNull String username,
        @NotBlank String prompt,
        String response
) {
}
