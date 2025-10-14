package com.sparta.delivery.restaurant.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SliceListResponseDto (
    List<RestaurantListResponseDto> restaurants,
    int nowPage,
    boolean hasNextPage
){}
