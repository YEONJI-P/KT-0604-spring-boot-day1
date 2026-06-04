package com.sesac.aibackend.controller;


import com.sesac.aibackend.domain.Game;
import com.sesac.aibackend.dto.GameRequest;
import com.sesac.aibackend.dto.GameResponse;
import com.sesac.aibackend.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public List<GameResponse> list() {
        return gameService.findAll().stream().map(GameResponse::from).toList();
    }

    @GetMapping("/{id}")
    public GameResponse get(@PathVariable Long id){
        return GameResponse.from(gameService.findById(id));
    }

    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest req){
        Game newGame = gameService.create(req);
        return ResponseEntity.created(URI.create("/games"+newGame.getId()))
                .body(GameResponse.from(newGame));
    }

    @PutMapping("/{id}")
    public GameResponse update(@PathVariable Long id, @Valid @RequestBody GameRequest req){
        return GameResponse.from(gameService.update(id,req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
