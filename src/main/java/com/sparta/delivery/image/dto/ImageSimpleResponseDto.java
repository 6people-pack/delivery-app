package com.sparta.delivery.image.dto;

import com.sparta.delivery.image.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ImageSimpleResponseDto {
    private String imageId;
    private int index;

    public ImageSimpleResponseDto(String imageId, int index) {
        this.imageId = imageId;
        this.index = index;
    }
}
