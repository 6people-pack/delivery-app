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

    // 유저의 가장 최근 주문 조회, 결재페이지에서 주문 중복 생성을 막기 위한 임시 코드
    Optional<Order> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
