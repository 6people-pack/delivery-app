package com.sparta.delivery.review.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "p_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id")
    private UUID id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Double rating;

    // FK 안 잡고 UUID만 보관 (코멘트 연동 테스트용 최소 셋)
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "restaurant_id")
    private UUID restaurantId;

    @Builder
    private Review(String content, Double rating, UUID orderId, Long userId, UUID restaurantId) {
        this.content = content;
        this.rating = rating;
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public static Review create(String content, Double rating, UUID orderId, Long userId, UUID restaurantId) {
        return Review.builder()
                .content(content)
                .rating(rating)
                .orderId(orderId)
                .userId(userId)
                .restaurantId(restaurantId)
                .build();
    }
}