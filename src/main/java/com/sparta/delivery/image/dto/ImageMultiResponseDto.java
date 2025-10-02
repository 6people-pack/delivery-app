package com.sparta.delivery.image.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;

import java.util.List;

@Getter
public class ImageMultiResponseDto {
    private String category;
    private String categoryId;
    private List<ImageSimpleResponseDto> images;

    public ImageMultiResponseDto(String category, String categoryId, List<ImageSimpleResponseDto> images) {
        this.category = category;
        this.categoryId = categoryId;
        this.images = images;
    }
}
