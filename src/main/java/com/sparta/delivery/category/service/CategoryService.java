package com.sparta.delivery.category.service;

import com.sparta.delivery.category.domain.Category;
import com.sparta.delivery.category.dto.CategoryRequestDto;
import com.sparta.delivery.category.dto.CategoryResponseDto;
import com.sparta.delivery.category.mapper.CategoryMapper;
import com.sparta.delivery.category.repository.CategoryRepository;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.user.domain.Role;
import com.sparta.delivery.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 등록
    @Transactional
    public void createCategory(User user, CategoryRequestDto requestDto) {
//        validateUser(user);
        if (categoryRepository.existsByName(requestDto.name())) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_EXISTS);
        }
        Category category = CategoryMapper.toCategory(requestDto);
        categoryRepository.save(category);
    }

    // 카테고리 수정
    @Transactional
    public void editCategory(User user, UUID categoryId, CategoryRequestDto requestDto) {
//        validateUser(user);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        if (categoryRepository.existsByNameAndIdNot(requestDto.name(), categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        category.editName(requestDto.name());
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(User user, UUID categoryId) {
//        validateUser(user);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.delete(user.getId());
    }

    // 카테고리 조회
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategories() {
        List<Category> categories = categoryRepository.findAllByDeletedAtIsNull();

        List<CategoryResponseDto> categoryList = new ArrayList<>();
        if (!categories.isEmpty()) {
            categoryList = categories.stream()
                    .map(CategoryResponseDto::new).toList();
            for (CategoryResponseDto categoryResponseDto : categoryList) {
                log.info(categoryResponseDto.toString());
            }
        }
        return categoryList;
    }
    
    // 사용자 권한 검증 (Admin 사용자만 카테고리 CUD 가능)
    private void validateUser(User user) {
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new BusinessException(ErrorCode.ROLE_AUTHORIZATION_REQUIRED);
        }
    }
}
