package com.sparta.delivery.restaurant.mapper;

import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.domain.RestaurantCategory;

import java.util.UUID;

public class RestaurantCategoryMapper {
    public static RestaurantCategory toRestaurantCategory(Restaurant restaurant, UUID categoryId) {
        return RestaurantCategory.builder()
                .restaurant(restaurant)
                .categoryId(categoryId)
                .build();
    }
}
