package com.sparta.delivery.review.controller;

import com.sparta.delivery.review.dto.ReviewCreateRequestDto;
import com.sparta.delivery.review.dto.ReviewResponseDto;
import com.sparta.delivery.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ReviewResponseDto create(@AuthenticationPrincipal(expression = "id") UUID loginUserId,
                                    @Valid @RequestBody ReviewCreateRequestDto req) {
        requireUser(loginUserId);
        return reviewService.create(loginUserId, req);
    }

    @GetMapping("/reviews/{reviewId}")
    public ReviewResponseDto get(@PathVariable UUID reviewId) {
        return reviewService.get(reviewId);
    }

    //가게 PK를 쿼리스트링으로 받아 리스트 조회
    @GetMapping("/reviews")
    public Page<ReviewResponseDto> list(@RequestParam UUID restaurantId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "NEWEST") String sort) {
        return reviewService.listByRestaurant(restaurantId, page, size, sort);
    }

    @PatchMapping("/reviews/{reviewId}")
    public ReviewResponseDto update(@AuthenticationPrincipal(expression = "id") UUID loginUserId,
                                    @PathVariable UUID reviewId,
                                    @Valid @RequestBody ReviewUpdateRequestDto req) {
        requireUser(loginUserId);
        return reviewService.update(loginUserId, reviewId, req);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public void delete(@AuthenticationPrincipal(expression = "id") UUID loginUserId,
                       @PathVariable UUID reviewId) {
        requireUser(loginUserId);
        reviewService.delete(loginUserId, reviewId);
    }

    private static void requireUser(UUID loginUserId) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
    }
}
