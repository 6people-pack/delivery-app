package com.sparta.delivery.restaurant.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Builder
    public RestaurantCategory(Restaurant restaurant, UUID categoryId) {
        this.restaurant = restaurant;
        this.categoryId = categoryId;
    }
}
