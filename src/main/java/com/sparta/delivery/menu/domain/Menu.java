package com.sparta.delivery.menu.domain;

import com.sparta.delivery.restaurant.domain.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_menu")
public class Menu {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "menu_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int discountPrice;

    @Column(columnDefinition = "TEXT")
    private String option;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MenuStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.discountPrice == 0) this.discountPrice = 0;
        if (this.status == null) this.status = MenuStatus.SALE;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    //PATCH 요청에서 null이 아닌 필드만 바꾸는 부분 수정 메서드
    public void update(String name, String description, Integer price, Integer discountPrice, String option, MenuStatus status) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (discountPrice != null) this.discountPrice = discountPrice;
        if (option != null) this.option = option;
        if (status != null) this.status = status;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
