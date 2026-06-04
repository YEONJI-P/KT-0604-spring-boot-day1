package com.sesac.aibackend.controller;


import com.sesac.aibackend.domain.Game;
import com.sesac.aibackend.dto.GameRequest;
import com.sesac.aibackend.dto.GameResponse;
import com.sesac.aibackend.error.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/games")
public class GameController {
    private final Map<Long, Game> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @GetMapping
    public List<GameResponse> list() { return storage.values().stream().map(GameResponse::from).toList();}

    @GetMapping("/{id}")
    public GameResponse get(@PathVariable Long id){
        Game game = storage.get(id);
        if(game == null){
            throw NotFoundException.of("game",id);
        }
        return GameResponse.from(game);
    }

    @PutMapping("/{id}")
    public GameResponse update(@PathVariable Long id, @Valid @RequestBody GameRequest req){
        Game existing = storage.get(id);
        if(existing==null){
            throw NotFoundException.of("item",id);
        }
        existing.setName(req.name());
        existing.setCategory(req.category());
        return GameResponse.from(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(storage.remove(id)==null){
            throw NotFoundException.of("Game",id);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest req){
        long id = sequence.getAndIncrement();
        Game saved = Game.builder()
                .id(id)
                .name(req.name())
                .category(req.category())
                .build();

        storage.put(id,saved);
        return ResponseEntity.created(URI.create("/games"+id))
                .body(GameResponse.from(saved));
    }
}
