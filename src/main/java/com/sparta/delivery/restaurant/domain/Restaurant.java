package com.sparta.delivery.restaurant.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_restaurant")
public class Restaurant {
    @Id
    @UuidGenerator
    @Column(name = "restaurant_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 60)
    private String name;
}
