package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// record 는 getter setter 자동 생성
// route 전 validation
public record ItemRequest(@NotBlank String name, @Min(0) int price) {
    public Item toEntity(){
        return Item.builder().name(name).price(price).build();
    }

}
