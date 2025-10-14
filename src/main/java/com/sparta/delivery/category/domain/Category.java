package com.sparta.delivery.category.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_category")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id")
    private UUID id;

    @Column(nullable = false, length = 20, unique = true)
    private String name;

    @Builder
    public Category(String name) {
        this.name = name;
    }

    public void editName(String name) {
        this.name = name;
    }
}
