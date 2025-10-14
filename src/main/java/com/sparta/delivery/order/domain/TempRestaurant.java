package com.sparta.delivery.order.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
public class TempRestaurant {

    @Id
    @Column(name = "restaurant_id", columnDefinition = "uuid")
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    private String name;
}
