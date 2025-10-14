package com.sparta.delivery.cartitem.repository;

import com.sparta.delivery.cartitem.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByIdAndUserId(UUID cartItemId, Long userId);
}
