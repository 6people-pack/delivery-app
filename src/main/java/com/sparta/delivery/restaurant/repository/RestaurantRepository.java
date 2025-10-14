package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
// existsById(UUID id) 기본 제공
}