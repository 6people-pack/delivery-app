package com.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_order_item")
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id", columnDefinition = "UUID")
    private UUID id;

    private String menuName;

    private int menuPrice;

    private int quantity;

    private String options;

    private UUID menuId;

    public OrderItem(String menuName, int menuPrice, int quantity, String options, UUID menuId) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.quantity = quantity;
        this.options = options;
        this.menuId = menuId;
    }

}
