package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Game;
import jakarta.validation.constraints.NotBlank;

public record GameRequest(@NotBlank String name, @NotBlank String category) {
    public Game toEntity(){
        return Game.builder()
                .name(name)
                .category(category)
                .build();
    }
}
