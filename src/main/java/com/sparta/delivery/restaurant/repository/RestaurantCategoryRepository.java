package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.domain.RestaurantCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, UUID> {
    // 기존 식당 카테고리 삭제 후 재생성(수정시 사용)
    void deleteAllByRestaurant(Restaurant findRestaurant);
}
