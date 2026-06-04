package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Game;

public record GameResponse(Long id, String name, String category) {
    public static GameResponse from(Game game){
        return new GameResponse(
                game.getId(),
                game.getName(),
                game.getCategory()
        );
    }
}
