package com.sparta.delivery.category.repository;

import com.sparta.delivery.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    List<Category> findAllByDeletedAtIsNull();
    Category findByName(String name);
}
