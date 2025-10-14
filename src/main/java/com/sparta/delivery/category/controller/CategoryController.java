package com.sparta.delivery.category.controller;

import com.sparta.delivery.category.dto.CategoryRequestDto;
import com.sparta.delivery.category.service.CategoryService;
import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/categories")
    public BaseResponse<?> createCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @Valid @RequestBody CategoryRequestDto requestDto) {
        categoryService.createCategory(userDetails.getUser(), requestDto);
        return BaseResponse.ok(BaseStatus.CREATED);
    }

    // 카테고리 수정
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/categories/{categoryId}")
    public BaseResponse<?> editCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable UUID categoryId,
                                        @Valid @RequestBody CategoryRequestDto requestDto) {
        categoryService.editCategory(userDetails.getUser(), categoryId, requestDto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 카테고리 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/categories/{categoryId}")
    public BaseResponse<?> deleteCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable UUID categoryId) {
        categoryService.deleteCategory(userDetails.getUser(), categoryId);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 카테고리 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/categories")
    public BaseResponse<?> getCategories() {
        return BaseResponse.ok(categoryService.getCategories(), BaseStatus.OK);
    }
}

