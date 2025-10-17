package com.sparta.delivery.comment.service;

import com.sparta.delivery.comment.domain.Comment;
import com.sparta.delivery.comment.dto.CommentCreateRequestDto;
import com.sparta.delivery.comment.dto.CommentResponseDto;
import com.sparta.delivery.comment.repository.CommentRepository;
import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.inquiry.repository.InquiryRepository;
import com.sparta.delivery.review.domain.Review;
import com.sparta.delivery.review.repository.ReviewRepository;
import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    /** 리뷰 댓글 생성 */
    @Transactional
    public CommentResponseDto createForReview(Long loginUserId, UUID reviewId, CommentCreateRequestDto req) {
        User user = userRepository.getReferenceById(loginUserId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없어요."));

        Comment parent = (req.parentId() == null) ? null
                : commentRepository.getReferenceById(req.parentId());

        Comment saved = commentRepository.save(
                Comment.forReview(req.content(), user, review, parent)
        );
        return CommentResponseDto.of(saved);
    }

    /** 문의 댓글 생성 */
    @Transactional
    public CommentResponseDto createForInquiry(Long loginUserId, UUID inquiryId, CommentCreateRequestDto req) {
        User user = userRepository.getReferenceById(loginUserId);
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 없어요."));

        Comment parent = (req.parentId() == null) ? null
                : commentRepository.getReferenceById(req.parentId());

        Comment saved = commentRepository.save(
                Comment.forInquiry(req.content(), user, inquiry, parent)
        );
        return CommentResponseDto.of(saved);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAiComment(String answer, User user, Inquiry inquiry) {
        commentRepository.save(Comment.forInquiry(answer, user, inquiry, null));
    }

    /** 리뷰 댓글 조회 */
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> listByReview(UUID reviewId, Pageable pageable) {
        return commentRepository.findByReview_Id(reviewId, pageable)
                .map(CommentResponseDto::of);
    }

    /** 문의 댓글 조회 */
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> listByInquiry(UUID inquiryId, Pageable pageable) {
        return commentRepository.findByInquiry_Id(inquiryId, pageable)
                .map(CommentResponseDto::of);
    }
}