package com.sparta.delivery.order.repository;

import com.sparta.delivery.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        select o.id as id, o.userId as userId, o.restaurantId as restaurantId, o.orderStatus as status
        from Order o
        where o.id = :orderId
    """)
    Optional<Order> findViewById(UUID orderId);

    List<Order> findByUserId(Long id);


    List<Order> findByRestaurantId(UUID restaurantId);
}
