package com.sparta.delivery.global.category;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.review.domain.Review;
import com.sparta.delivery.review.repository.ReviewRepository;
import com.sparta.delivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryCheck {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;

    //카테고리에 따른 권한 체크
    public void checkAuthority(Long userId, ImageCategory imageCategory, UUID categoryid) {
        switch (imageCategory) {
            case restaurant -> {
                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                Restaurant restaurant = restaurantRepository.findById(categoryid).orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
                if(!restaurant.getOwnerId().equals(userId)) throw new BusinessException(ErrorCode.NOT_OWNER);
            }
            case menu -> {
                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                Menu menu = menuRepository.findById(categoryid).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
                Restaurant restaurant = restaurantRepository.findById(menu.getRestaurant().getId()).orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
                if(!restaurant.getOwnerId().equals(userId)) throw new BusinessException(ErrorCode.NOT_OWNER);
            }
            case review -> {
                userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                Review review = reviewRepository.findById(categoryid).orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
                if(!review.getUserId().equals(userId)) throw new BusinessException(ErrorCode.NOT_REVIEWER);
            }
            default -> {}
        }
    }
}
