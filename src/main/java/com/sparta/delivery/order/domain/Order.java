package com.sparta.delivery.order.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_id", columnDefinition = "UUID")
    private UUID orderId;

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
    private UUID restaurantId;   // 임시, 나중에 식당으로

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
    private LocalDateTime orderedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "delivering_at")
    private LocalDateTime deliveringAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_message", columnDefinition = "TEXT")
    private String cancelMessage;

    @OneToMany
    @JoinColumn(name = "order_id") // 외래키를 OrderItem 테이블에 생성
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(String orderNumber, String address, String addressDetail, Long userId, UUID restaurantId, int grossAmount, int vat, int deliveryFee,
                 int discountAmount, int totalAmount, String customerRequest, List<OrderItem> orderItems, LocalDateTime orderedAt) {

        this.orderNumber = orderNumber;
        this.orderStatus = OrderStatus.REQUESTED;
        this.address = address;
        this.addressDetail = addressDetail;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.grossAmount = grossAmount;
        this.vat = vat;
        this.deliveryFee = deliveryFee;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.customerRequest = customerRequest;
        this.orderItems = orderItems;
        this.orderedAt = orderedAt;

    }

    public void changeStatusAccepted() {
        this.acceptedAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.ACCEPTED;
    }

    public void changeStatusDelivering(){
        this.deliveringAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.DELIVERING;
    }

    public void changeStatusDelivered(){
        this.deliveredAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.DELIVERED;
    }

    public void changeStatusCanceled() {
        this.canceledAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void changeStatusCanceled(String  cancelMessage) {
        this.canceledAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.CANCELED;
        this.cancelMessage = cancelMessage;
    }

}

