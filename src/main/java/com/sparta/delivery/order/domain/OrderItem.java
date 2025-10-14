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
    @Column(name = "order_item_id", columnDefinition = "uuid")
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    private String menuName;

    private int menuPrice;

    private int quantity;

    private String option;

    private UUID menuId;

    public OrderItem(String menuName, int menuPrice, int quantity, String option, UUID menuId) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.quantity = quantity;
        this.option = option;
        this.menuId = menuId;
    }

}
