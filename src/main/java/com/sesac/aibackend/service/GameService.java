package com.sesac.aibackend.service;


import com.sesac.aibackend.domain.Game;
import com.sesac.aibackend.dto.GameRequest;
import com.sesac.aibackend.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class GameService {
    private final Map<Long, Game> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public List<Game> findAll(){
        return storage.values().stream().toList();
    }

    public Game findById(Long id){
        Game game = storage.get(id);
        if(game==null){
            throw NotFoundException.of("game",id);
        }
        return game;
    }

    public Game create(GameRequest request){
        long id = sequence.getAndIncrement();
        Game saved = Game.builder()
                .id(id)
                .name(request.name())
                .category(request.category())
                .build();
        storage.put(id,saved);
        return saved;
    }

    public Game update(Long id,GameRequest req){
        Game existing = storage.get(id);
        if(existing==null){
            throw NotFoundException.of("item",id);
        }
        existing.setName(req.name());
        existing.setCategory(req.category());

        return existing;
    }

    public void delete(Long id){
        Game game = storage.get(id);
        if(game == null){
            throw NotFoundException.of("game",id);
        }
    }



}
