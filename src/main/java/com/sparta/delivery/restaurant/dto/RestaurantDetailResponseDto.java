package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.restaurant.domain.Restaurant;
import lombok.Getter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
public class RestaurantDetailResponseDto {
    private UUID restaurantId;
    private String address;
    private String addressDetail;
    private String phoneNumber;
    private String businessNumber;
    private String name;
    private String description;
    private int minOrderPrice;
    private Boolean isOpen;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Double rating;

    public RestaurantDetailResponseDto(Restaurant restaurant) {
        this.restaurantId = restaurant.getId();
        this.address = restaurant.getAddress();
        this.addressDetail = restaurant.getAddressDetail();
        this.phoneNumber = restaurant.getPhoneNumber();
        this.businessNumber = restaurant.getBusinessNumber();
        this.name = restaurant.getName();
        this.description = restaurant.getDescription();
        this.minOrderPrice = restaurant.getMinOrderPrice();
        this.isOpen = restaurant.isOpen();
        this.openTime = restaurant.getOpenTime();
        this.closeTime = restaurant.getCloseTime();
        this.rating = restaurant.getRating();
    }
}
