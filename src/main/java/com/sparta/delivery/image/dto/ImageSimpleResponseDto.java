package com.sparta.delivery.image.dto;

import lombok.Getter;

@Getter
public class ImageSimpleResponseDto {
    private String imageUrl;
    private int index;

    public ImageSimpleResponseDto(String imageUrl, int index) {
        this.imageUrl = imageUrl;
        this.index = index;
    }
}
