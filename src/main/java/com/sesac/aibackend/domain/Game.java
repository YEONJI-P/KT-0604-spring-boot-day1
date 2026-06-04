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

    // mobile or PC 추후 boolean 이나 enum 으로 change ?
    // 둘 다 지원할 경우는 어떻게 처리할지
//    private String device;
    // mobile > ios or android
    // PC > windows or mac or Linux
    // 크로스플랫폼일 경우 처리 방법 구상
//    private String platform;

//    private int price;

}

