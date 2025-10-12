package com.sparta.delivery.restaurant.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_restaurant")
public class Restaurant {
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY) // MySQL BINARY(16)
    @Column(name = "restaurant_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 60)
    private String name;
}
