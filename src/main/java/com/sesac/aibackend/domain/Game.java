package com.sesac.aibackend.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Game {
    private Long id;
    private String name;
    private String category;
}
