package com.sparta.delivery.image.domain;

import com.sparta.delivery.global.category.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="p_images")
@NoArgsConstructor
public class Image {
    @Id
    @Column(name="image_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private int index;

    public Image(UUID id, Category category, UUID categoryId, String url, int index) {
        this.id = id;
        this.category = category;
        this.categoryId = categoryId;
        this.url = url;
        this.index = index;
    }


    public void updateIndex(int newIndex) {
        this.index = newIndex;
    }
}
