package com.sparta.delivery.restaurant.mapper;

import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.dto.RestaurantDetailResponseDto;
import com.sparta.delivery.restaurant.dto.RestaurantListResponseDto;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.dto.SliceListResponseDto;

import java.util.List;

public class RestaurantMapper {
    public static Restaurant toRestaurant(Long userId, RestaurantRequestDto requestDto) {
        return Restaurant.builder()
                .ownerId(userId)
                .name(requestDto.name())
                .latitude(requestDto.latitude())
                .longitude(requestDto.longitude())
                .address(requestDto.address())
                .addressDetail(requestDto.addressDetail())
                .phoneNumber(requestDto.phoneNumber())
                .businessNumber(requestDto.businessNumber())
                .description(requestDto.description())
                .minOrderPrice(requestDto.minOrderPrice())
                .isOpen(requestDto.isOpen())
                .openTime(requestDto.openTime())
                .closeTime(requestDto.closeTime())
                .build();
    }

    public static RestaurantDetailResponseDto toRestaurantDetailResponseDto(Restaurant restaurant) {
        return RestaurantDetailResponseDto.builder()
                .restaurantId(restaurant.getId())
                .description(restaurant.getDescription())
                .minOrderPrice(restaurant.getMinOrderPrice())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .addressDetail(restaurant.getAddressDetail())
                .phoneNumber(restaurant.getPhoneNumber())
                .businessNumber(restaurant.getBusinessNumber())
                .isOpen(restaurant.isOpen())
                .openTime(restaurant.getOpenTime())
                .closeTime(restaurant.getCloseTime())
                .rating(restaurant.getRating())
                .build();
    }

    public static SliceListResponseDto toSliceListResponseDto(List<RestaurantListResponseDto> restaurants, int page, boolean hasNext) {
        return SliceListResponseDto.builder()
                .restaurants(restaurants)
                .nowPage(page)
                .hasNextPage(hasNext)
                .build();

    }


}
