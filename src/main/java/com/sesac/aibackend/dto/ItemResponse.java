package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Item;

// entity 를 노출하지 않고 내보낼 필드만 dto 로 변환
public record ItemResponse(Long id, String name, int price) {
    public static ItemResponse from(Item item){
        return new ItemResponse(item.getId(),item.getName(),item.getPrice());
    }
}
