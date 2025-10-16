package com.sparta.delivery.order.repository;

import com.sparta.delivery.order.domain.Order;
import com.sparta.delivery.order.repository.view.OrderView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        select o.id as id, o.userId as userId, o.restaurantId as restaurantId, o.orderStatus as status
        from Order o
        where o.id = :orderId
    """)
    Optional<OrderView> findViewById(UUID orderId);

    Page<Order> findByUserId(Long id, Pageable pageable);

    Page<Order> findByRestaurantId(UUID restaurantId, Pageable pageable);
}
