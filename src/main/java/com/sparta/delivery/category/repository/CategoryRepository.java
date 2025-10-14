package com.sparta.delivery.category.repository;

import com.sparta.delivery.category.domain.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // 중복 이름 검증 (등록시 사용)
    boolean existsByName(String name);
    
    // 중복 이름 검증 (수정시 사용)
    boolean existsByNameAndIdNot(String name, UUID categoryId);

    // 삭제되지않은 모든 카테고리 조회 (조회시 사용)
    List<Category> findAllByDeletedAtIsNull();
}
