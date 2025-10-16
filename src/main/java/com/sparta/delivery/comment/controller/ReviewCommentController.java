package com.sparta.delivery.comment.controller;

import com.sparta.delivery.comment.dto.CommentCreateRequestDto;
import com.sparta.delivery.comment.dto.CommentResponseDto;
import com.sparta.delivery.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/comments")
public class ReviewCommentController {
    private final CommentService service;

    @PostMapping
    public CommentResponseDto create(
            @AuthenticationPrincipal(expression = "user.id") Long userId,
            @PathVariable("reviewId") UUID reviewId,
            @Valid @RequestBody CommentCreateRequestDto req
    ) {
        return service.createForReview(userId, reviewId, req);
    }

    @GetMapping
    public Page<CommentResponseDto> list(
            @PathVariable("reviewId") UUID reviewId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.listByReview(reviewId, pageable);
    }
}