//package com.sparta.delivery.comment.dto;
// 대댓글 지원 DTO
//import com.sparta.delivery.comment.domain.Comment;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//public record CommentResponseDto(
//        UUID id,
//        String content,
//        String authorNickname,
//        UUID parentId,
//        LocalDateTime createdAt
//) {
//    public static CommentResponseDto of(Comment c) {
//        return new CommentResponseDto(
//                c.getId(),
//                c.getContent(),
//                c.getAuthor().getNickname(),
//                c.getParent() == null ? null : c.getParent().getId(),
//                c.getCreatedAt()
//        );
//    }
//}
