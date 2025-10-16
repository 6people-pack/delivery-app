package com.sparta.delivery.image.repository;

import com.sparta.delivery.global.category.ImageCategory;
import com.sparta.delivery.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findAllByCategoryAndCategoryIdOrderByIndexAsc(ImageCategory imageCategory, UUID uuid);
     int countByCategoryAndCategoryId(ImageCategory imageCategory, UUID categoryId);
     void deleteAllByCategoryAndCategoryId(ImageCategory imageCategory, UUID categoryId);
}
