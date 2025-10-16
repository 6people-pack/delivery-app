package com.sparta.delivery.comment.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import com.sparta.delivery.inquiry.domain.Inquiry;
import com.sparta.delivery.review.domain.Review;
import com.sparta.delivery.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "comment_id")
    private UUID id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 작성자 (User PK = Long, DB = BIGINT) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 리뷰 연결 (리뷰 PK = UUID) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    /** 문의 연결 (문의 PK = UUID) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    private Inquiry inquiry;

    /** 대댓글 (자기 참조) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(String content, User user, Review review, Inquiry inquiry, Comment parent) {
        this.content = content;
        this.user = user;
        this.review = review;
        this.inquiry = inquiry;
        this.parent = parent;

        // 리뷰/문의 둘 다 세팅되거나 둘 다 null이면 잘못된 상태
        if ((review == null && inquiry == null) || (review != null && inquiry != null)) {
            throw new IllegalArgumentException("리뷰 또는 문의 중 하나에만 연결되어야 합니다.");
        }
    }

    /** 리뷰용 댓글 팩토리 */
    public static Comment forReview(String content, User user, Review review, Comment parent) {
        return Comment.builder()
                .content(content)
                .user(user)
                .review(review)
                .parent(parent)
                .build();
    }

    /** 문의용 댓글 팩토리 */
    public static Comment forInquiry(String content, User user, Inquiry inquiry, Comment parent) {
        return Comment.builder()
                .content(content)
                .user(user)
                .inquiry(inquiry)
                .parent(parent)
                .build();
    }
}