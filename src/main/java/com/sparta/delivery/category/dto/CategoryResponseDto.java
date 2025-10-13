package com.sparta.delivery.category.dto;

import com.sparta.delivery.category.domain.Category;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryResponseDto {
    private UUID categoryId;
    private String name;

    public CategoryResponseDto(Category category) {
        this.categoryId = category.getId();
        this.name = category.getName();
    }
}
