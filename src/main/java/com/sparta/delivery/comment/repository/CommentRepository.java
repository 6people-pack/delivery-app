package com.sparta.delivery.comment.repository;

import com.sparta.delivery.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByReview_Id(UUID reviewId, Pageable pageable);
    Page<Comment> findByInquiry_Id(UUID inquiryId, Pageable pageable);
    Page<Comment> findByParent_Id(UUID parentId, Pageable pageable);
}
