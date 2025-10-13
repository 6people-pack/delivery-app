package com.sparta.delivery.restaurant.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "restaurant_id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 20, nullable = false)
    private String address;

    @Column(length = 20, nullable = false)
    private String addressDetail;

    @Column(length = 20, nullable = false)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String businessNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int minOrderPrice;

    @Column(nullable = false)
    private boolean isOpen;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    private Double rating;

    private Double totalRating;

    private int reviewCount;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantCategory> restaurantCategories = new ArrayList<>();

    @Builder
    public Restaurant(Long ownerId, Double latitude, Double longitude, String address, String addressDetail,
                      String phoneNumber, String businessNumber, String name, String description, int minOrderPrice,
                      boolean isOpen, LocalTime openTime, LocalTime closeTime) {
        this.ownerId = ownerId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.addressDetail = addressDetail;
        this.phoneNumber = phoneNumber;
        this.businessNumber = businessNumber;
        this.name = name;
        this.description = description;
        this.minOrderPrice = minOrderPrice;
        this.isOpen = isOpen;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.totalRating = 0.0;
        this.reviewCount = 0;
        this.rating = 0.0;
    }

    public void editRestaurant(RestaurantRequestDto requestDto, String description) {
        this.latitude = requestDto.latitude();
        this.longitude = requestDto.longitude();
        this.address = requestDto.address();
        this.addressDetail = requestDto.addressDetail();
        this.phoneNumber = requestDto.phoneNumber();
        this.businessNumber = requestDto.businessNumber();
        this.name = requestDto.name();
        this.description = description;
        this.minOrderPrice = requestDto.minOrderPrice();
        this.isOpen = requestDto.isOpen();
        this.openTime = requestDto.openTime();
        this.closeTime = requestDto.closeTime();
    }

    public void updateRating(double rating) {
        totalRating += rating;
        reviewCount++;
        this.rating = (double) Math.round((totalRating / reviewCount) * 10) / 10.0;
    }

    public void deleteRating(Double rating) {
        totalRating -= rating;
        reviewCount--;
        this.rating = (double) Math.round((totalRating / reviewCount) * 10) / 10.0;
    }

    public void editCategories(List<UUID> categories) {

    }
}
