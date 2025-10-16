package com.sparta.delivery.image.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ImageUpdateRequestDto {
    private String category;
    private String categoryId;
    private List<ImageCDto> create;
    private List<ImageDDto> delete;
    private List<ImageUDto> update;
}

