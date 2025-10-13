package com.sparta.delivery.restaurant.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SliceListResponseDto {
    private List<RestaurantListResponseDto> restaurants;
    private int nowPage;
    private boolean hasNextPage;
}
