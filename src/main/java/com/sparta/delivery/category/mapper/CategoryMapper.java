package com.sparta.delivery.category.mapper;

import com.sparta.delivery.category.domain.Category;
import com.sparta.delivery.category.dto.CategoryRequestDto;

public class CategoryMapper {
    public static Category toCategory(CategoryRequestDto requestDto) {
        return Category.builder()
                .name(requestDto.name())
                .build();
    }
}
