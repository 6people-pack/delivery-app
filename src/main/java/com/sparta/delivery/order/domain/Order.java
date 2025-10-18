package com.sparta.delivery.order.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_order")
@Where(clause = "deleted_at IS NULL")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "order_number", length = 30, nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "delivery_address", length = 20)
    private String address;

    @Column(name = "delivery_address_detail", length = 20)
    private String addressDetail;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "restaurant_id", columnDefinition = "UUID")
    private UUID restaurantId;

    @Column(name = "gross_amount")
    private int grossAmount;

    @Column(name = "vat")
    private int vat;

    @Column(name = "delivery_fee")
    private int deliveryFee;

    @Column(name = "discount_amount")
    private int discountAmount;

    @Column(name = "total_amount")
    private int totalAmount;

    @Column(name = "customer_request", columnDefinition = "TEXT")
    private String customerRequest;

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt; // 주문 생성 시각

    @Column(name = "requested_at")
    private LocalDateTime requestedAt; // 주문 요청 시각(결재 완료 시각과 동일)

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt; // 주문 수락 시각

    @Column(name = "delivering_at")
    private LocalDateTime deliveringAt; // 배달 시작 시각

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt; // 배달 완료 시각

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt; // 주문 취소 시각

    @Column(name = "cancel_message", columnDefinition = "TEXT")
    private String cancelMessage;

    @OneToMany
    @JoinColumn(name = "order_id") // 외래키를 OrderItem 테이블에 생성
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order create(
            String orderNumber,
            String address,
            String addressDetail,
            Long userId,
            UUID restaurantId,
            int grossAmount,
            int vat,
            int deliveryFee,
            int discountAmount,
            int totalAmount,
            String customerRequest,
            List<OrderItem> orderItems,
            LocalDateTime orderedAt
    ) {
        Order order = new Order();
        order.orderNumber = orderNumber;
        order.orderStatus = OrderStatus.PENDING; // 결재 대기 상태
        order.address = address;
        order.addressDetail = addressDetail;
        order.userId = userId;
        order.restaurantId = restaurantId;
        order.grossAmount = grossAmount;
        order.vat = vat;
        order.deliveryFee = deliveryFee;
        order.discountAmount = discountAmount;
        order.totalAmount = totalAmount;
        order.customerRequest = customerRequest;
        order.orderItems = orderItems;
        order.orderedAt = orderedAt;
        return order;
    }

    // 주문 요청 시(결재 승인과 동시에 요청)
    public void changeStatusRequested(){
        this.requestedAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.REQUESTED;
    }

    // 주문 수락 시
    public void changeStatusAccepted() {
        this.acceptedAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.ACCEPTED;
    }

    // 배달 시작 시
    public void changeStatusDelivering(){
        this.deliveringAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.DELIVERING;
    }

    // 배달 완료 시
    public void changeStatusDelivered(){
        this.deliveredAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.DELIVERED;
    }

    // 점주 취소 시, 메세지
    public void changeStatusCanceled(String  cancelMessage) {
        this.canceledAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.CANCELED;
        this.cancelMessage = cancelMessage;
    }

}

