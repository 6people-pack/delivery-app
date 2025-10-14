package com.sparta.delivery.review.repository;

import com.sparta.delivery.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByRestaurantIdAndDeletedAtIsNull(UUID restaurantId, Pageable pageable);
    Optional<Review> findByIdAndDeletedAtIsNull(UUID id);
    boolean existsByOrderIdAndUserId(UUID orderId, UUID userId);
}
