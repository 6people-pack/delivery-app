package com.sparta.delivery.restaurant.dto;

import java.util.UUID;

// nativeQuery Dto 변환을 위한 interface 선언
public interface RestaurantListResponseDto {
    UUID getRestaurantId();
    String getName();
    int getMinOrderPrice();
    boolean getIsOpen();
    double getRating();
    double getDistance();
}
