package com.sparta.delivery.cartitem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempMenu {

    // 임시 엔티티
    @Id
    @GeneratedValue
    @Column(name = "menu_id", updatable = false, nullable = false)
    private UUID id;

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
