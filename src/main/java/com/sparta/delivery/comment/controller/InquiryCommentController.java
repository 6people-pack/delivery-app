package com.sparta.delivery.comment.controller;

import com.sparta.delivery.comment.dto.CommentCreateRequestDto;
import com.sparta.delivery.comment.dto.CommentResponseDto;
import com.sparta.delivery.comment.service.CommentService;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
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
@RequestMapping("/api/inquiries/{inquiryId}/comments")
public class InquiryCommentController {

    private final CommentService service;

    @PostMapping
    public CommentResponseDto create(@AuthenticationPrincipal(expression = "user.id") Long userId, @PathVariable UUID inquiryId, @Valid @RequestBody CommentCreateRequestDto req) {
        return service.createForInquiry(userId, inquiryId, req);
    }

    @GetMapping
    public Page<CommentResponseDto> list(@PathVariable UUID inquiryId, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listByInquiry(inquiryId, pageable);
    }
}
