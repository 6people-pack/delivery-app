package com.sparta.delivery.image.dto;

import com.sparta.delivery.image.domain.Image;
import lombok.Getter;

@Getter
public class ImageResponseDto {
    private String imageId;
    private String category;
    private String categoryId;
    private int index;

    public ImageResponseDto(Image image) {
        this.imageId = image.getId().toString();
        this.category = image.getCategory().toString();
        this.categoryId = image.getCategoryId().toString();
        this.index = image.getIndex();
    }
}
