package com.sparta.delivery.image.repository;

import com.sparta.delivery.image.domain.Category;
import com.sparta.delivery.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findByCategoryAndCategoryIdAndIndex(Category category, UUID uuid, int index);
    List<Image> findAllByCategoryAndCategoryIdOrderByIndexAsc(Category category, UUID uuid);
     int countByCategoryAndCategoryId(Category category, UUID categoryId);

     List<Image> findAllByCategoryAndCategoryIdAndIndexBetween(Category category, UUID categoryId, int startIndex, int endIndex);
}
