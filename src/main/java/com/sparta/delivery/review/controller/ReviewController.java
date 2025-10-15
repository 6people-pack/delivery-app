package com.sparta.delivery.review.controller;

import com.sparta.delivery.review.dto.ReviewCreateRequestDto;
import com.sparta.delivery.review.dto.ReviewResponseDto;
import com.sparta.delivery.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.review.service.ReviewService;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.net.http.HttpResponse;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ReviewResponseDto create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @Valid @RequestBody ReviewCreateRequestDto req) {
        requireUser(userDetails.getUser().getId());
        return reviewService.create(userDetails.getUser(), req);
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
    public ReviewResponseDto update(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @PathVariable UUID reviewId,
                                    @Valid @RequestBody ReviewUpdateRequestDto req) {
        requireUser(userDetails.getUser().getId());
        return reviewService.update(userDetails.getUser(), reviewId, req);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public void delete(@AuthenticationPrincipal UserDetailsImpl userDetails,
                       @PathVariable UUID reviewId) {
        requireUser(userDetails.getUser().getId());
        reviewService.delete(userDetails.getUser(), reviewId);
    }

    private static void requireUser(Long loginUserId) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
    }
}
