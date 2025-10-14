package com.sparta.delivery.review.service;

import com.sparta.delivery.order.domain.OrderStatus;
import com.sparta.delivery.order.repository.OrderRepository;
import com.sparta.delivery.order.repository.view.OrderView;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.review.domain.Review;
import com.sparta.delivery.review.dto.ReviewCreateRequestDto;
import com.sparta.delivery.review.dto.ReviewResponseDto;
import com.sparta.delivery.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public ReviewResponseDto create(UUID loginUserId, ReviewCreateRequestDto req) {
        //주문 요약 조회
        OrderView order = orderRepository.findViewById(req.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        //본인 주문 여부
        if (!order.getUserId().equals(loginUserId)) {
            throw new SecurityException("본인 주문에만 리뷰 작성 가능합니다.");
        }

        //주문/배송 완료 상태 확인
        OrderStatus st = order.getStatus();
        if (!(st == OrderStatus.DELIVERED || st == OrderStatus.COMPLETED)) {
            throw new IllegalStateException("주문/배송 완료 이후에만 리뷰 작성 가능합니다.");
        }

        //가게 일치 검증
        if (!order.getRestaurantId().equals(req.restaurantId())) {
            throw new IllegalStateException("주문 가게와 리뷰 대상 가게가 일치하지 않습니다.");
        }

        //중복 리뷰 방지
        if (reviewRepository.existsByOrderIdAndUserId(req.orderId(), loginUserId)) {
            throw new IllegalStateException("해당 주문에 이미 리뷰가 존재합니다.");
        }

        Review saved = reviewRepository.save(
                Review.builder()
                        .restaurantId(req.restaurantId())
                        .userId(loginUserId)
                        .orderId(req.orderId())
                        .rating(req.rating())
                        .content(req.content())
                        .build()
        );

        return new ReviewResponseDto(
                saved.getId(), saved.getRestaurantId(), saved.getUserId(),
                saved.getRating(), saved.getContent()
        );
    }

    public ReviewResponseDto get(UUID reviewId) {
        var r = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        return new ReviewResponseDto(r.getId(), r.getRestaurantId(), r.getUserId(), r.getRating(), r.getContent());
    }

    public Page<ReviewResponseDto> listByRestaurant(UUID restaurantId, int page, int size, String sort) {
        Sort s = switch (sort) {
            case "RATING_DESC" -> Sort.by(Sort.Direction.DESC, "rating");
            case "RATING_ASC"  -> Sort.by(Sort.Direction.ASC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        return reviewRepository.findByRestaurantIdAndDeletedAtIsNull(restaurantId, PageRequest.of(page, size, s))
                .map(r -> new ReviewResponseDto(r.getId(), r.getRestaurantId(), r.getUserId(), r.getRating(), r.getContent()));
    }

    public ReviewResponseDto update(UUID loginUserId, UUID reviewId, ReviewUpdateRequestDto req) {
        var r = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        if (!r.getUserId().equals(loginUserId)) throw new SecurityException("본인 리뷰만 수정할 수 있습니다.");
        r.update(req.rating(), req.content());
        return new ReviewResponseDto(r.getId(), r.getRestaurantId(), r.getUserId(), r.getRating(), r.getContent());
    }

    public void delete(UUID loginUserId, UUID reviewId) {
        var r = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        if (!r.getUserId().equals(loginUserId)) throw new SecurityException("본인 리뷰만 삭제할 수 있습니다.");

        r.delete(null);
    }
}