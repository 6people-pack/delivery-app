//package com.sparta.delivery.comment.dto;
//대댓글 지원 DTO
//import jakarta.validation.constraints.NotBlank;
//import java.util.UUID;
//
//public record CommentCreateRequestDto(
//        @NotBlank String content,
//        UUID parentId      // 대댓글이면 전달, 아니면 null
//) {}