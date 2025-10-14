package com.sparta.delivery.category.mapper;

import com.sparta.delivery.category.domain.Category;
import com.sparta.delivery.category.dto.CategoryRequestDto;
import com.sparta.delivery.category.dto.CategoryResponseDto;

public class CategoryMapper {
    public static Category toCategory(CategoryRequestDto requestDto) {
        return Category.builder()
                .name(requestDto.name())
                .build();
    }

    public static CategoryResponseDto toCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .build();
    }
}
