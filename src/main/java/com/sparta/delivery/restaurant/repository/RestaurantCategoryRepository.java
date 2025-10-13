package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.domain.RestaurantCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, UUID> {
    void deleteAllByRestaurant(Restaurant findRestaurant);
}
