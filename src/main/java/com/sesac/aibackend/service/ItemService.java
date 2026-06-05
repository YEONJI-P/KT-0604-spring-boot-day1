package com.sesac.aibackend.service;

import com.sesac.aibackend.domain.Item;
import com.sesac.aibackend.dto.ItemRequest;
import com.sesac.aibackend.dto.ItemResponse;
import com.sesac.aibackend.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<Item> findAll() { return itemRepository.findAll(); }

    public Optional<Item> findById(Long id) { return itemRepository.findById(id); }

    public Item save(Item item) { return itemRepository.save(item); }

    public boolean existsById(Long id) { return itemRepository.existsById(id); }

    public void deleteById(Long id) { itemRepository.deleteById(id); }



}
