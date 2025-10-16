package com.sparta.delivery.order.repository.view;

import com.sparta.delivery.order.domain.OrderStatus;

import java.util.UUID;

public interface OrderView {
    UUID getId();
    Long getUserId();
    UUID getRestaurantId();
    OrderStatus getStatus();
}
