package com.sparta.delivery.cartitem.repository;

import com.sparta.delivery.cartitem.domain.CartItem;
import com.sparta.delivery.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUser(User user);

    Optional<CartItem> findByIdAndUser(UUID cartItemId, User user);
}
