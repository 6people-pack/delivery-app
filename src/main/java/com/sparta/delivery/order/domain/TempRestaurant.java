package com.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.UUID;

@Entity
@Getter
public class TempRestaurant {
    @Id
    @GeneratedValue
    @Column(name = "restaurant_id", columnDefinition = "UUID")
    private UUID id;

    private String name;
}
