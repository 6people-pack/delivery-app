package com.sparta.delivery.review.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "p_review",
        uniqueConstraints = @UniqueConstraint(name = "uk_review_order_user", columnNames = {"order_id","user_id"}),
        indexes = {
                @Index(name = "idx_review_restaurant", columnList = "restaurant_id"),
                @Index(name = "idx_review_user", columnList = "user_id")
        }
)

public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="review_id", nullable=false, updatable=false)
    private UUID id;

    @Column(name="content", nullable=false, columnDefinition="text")
    private String content;

    @DecimalMin("1.0") @DecimalMax("5.0")
    @Column(name="rating", nullable=false)
    private double rating;

    @Column(name="order_id", nullable=false)
    private UUID orderId;

    @Column(name="user_id", nullable=false)
    private UUID userId;

    @Column(name="restaurant_id", nullable=false)
    private UUID restaurantId;

    @Builder
    private Review(double rating, String content, UUID orderId, UUID userId, UUID restaurantId) {
        this.rating = rating;
        this.content = content;
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public void update(Double rating, String content) {
        if (rating != null) this.rating = rating;
        if (content != null) this.content = content;
    }
}
