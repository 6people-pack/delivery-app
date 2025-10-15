package com.sparta.delivery.comment.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import com.sparta.delivery.review.domain.Review;
import com.sparta.delivery.inquiry.domain.Inquiry;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User author;

    // 둘 중 하나만 채워짐 (CHECK 제약으로 보증)
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    private Inquiry inquiry;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(String content, User author, Review review, Inquiry inquiry, Comment parent) {
        this.content = content;
        this.author = author;
        this.review = review;
        this.inquiry = inquiry;
//        this.parent = parent;  //대댓 주석 해제
        // 런타임 방어(DDL에서 2중 보증)
        if ((review == null && inquiry == null) || (review != null && inquiry != null)) {
            throw new IllegalArgumentException("리뷰/문의 중 하나에만 연결되어야 합니다.");
        }
    }

    public static Comment forReview(String content, User author, Review review, Comment parent) {
        return Comment.builder().content(content).author(author).review(review).parent(parent).build();
    }

    public static Comment forInquiry(String content, User author, Inquiry inquiry, Comment parent) {
        return Comment.builder().content(content).author(author).inquiry(inquiry).parent(parent).build();
    }
}