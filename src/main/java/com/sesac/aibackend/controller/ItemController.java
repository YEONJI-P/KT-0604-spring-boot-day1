package com.sesac.aibackend.controller;

import com.sesac.aibackend.domain.Item;
import com.sesac.aibackend.dto.ItemRequest;
import com.sesac.aibackend.dto.ItemResponse;
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
@RequestMapping("/legacy/items")
public class ItemController {

    // db 역할
    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @GetMapping
    public List<ItemResponse> list(){
        return storage.values().stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id){
        Item item = storage.get(id);
        if(item==null){
            throw NotFoundException.of("item",id);
        }
        return ItemResponse.from(item);
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody ItemRequest req){
        Item existing = storage.get(id);
        if(existing == null){
            throw NotFoundException.of("item",id);
        }
        existing.setName(req.name());
        existing.setPrice(req.price());
        return ItemResponse.from(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if(storage.remove(id)==null){
          throw NotFoundException.of("item",id);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest req){
        // id 할당 및 증가
        long id = sequence.getAndIncrement();
        // request 데이터를 Item 객체로 변환
        Item saved = Item.builder().id(id).name(req.name()).price(req.price()).build();
        // storage 에 저장
        storage.put(id,saved);
        // Item객체를 ItemResponse dto 로 변환 후 지정된 uri 의 response의 body 로 ...
        return ResponseEntity.created(URI.create("/legacy/items"+id))
                .body(ItemResponse.from(saved));
    }


}
