package com.sparta.delivery.comment.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CommentCreateRequestDto(
        @NotBlank String content,
        UUID parentId,     // 대댓글이면 전달, 아니면 null
        Long ownerId   // 사장의 경우 해당 식당의 사장 ID 값 전달
) {}