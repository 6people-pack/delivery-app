package com.sparta.delivery.cartitem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempMenu {

    @Id
    @Column(name = "menu_id", columnDefinition = "uuid")
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @Column(length = 30, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(name = "discount_price")
    private int discountPrice;

    @Column(columnDefinition = "TEXT")
    private String option;

    @Column(nullable = false)
    private boolean status;

    private UUID restaurantId;


}
