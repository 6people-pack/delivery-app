package com.sparta.delivery.restaurant.service;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.restaurant.domain.RatingStatus;
import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.domain.RestaurantCategory;
import com.sparta.delivery.restaurant.dto.*;
import com.sparta.delivery.restaurant.mapper.RestaurantCategoryMapper;
import com.sparta.delivery.restaurant.mapper.RestaurantMapper;
import com.sparta.delivery.restaurant.repository.RestaurantCategoryRepository;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.domain.Role;
import com.sparta.delivery.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;

    // 식당 등록
    @Transactional
    public void createRestaurant(User user, RestaurantRequestDto requestDto, List<MultipartFile> restaurantImage) {
//        validateUser(user);
        if (restaurantRepository.existsByBusinessNumber(requestDto.businessNumber())) {
            throw new BusinessException(ErrorCode.BUSINESS_CODE_EXISTS);
        }

        // Todo : 이미지 저장
        Restaurant restaurant = RestaurantMapper.toRestaurant(user.getId(), requestDto);
        restaurantRepository.save(restaurant);

        ArrayList<RestaurantCategory> allCategories = new ArrayList<>();
        for (UUID category : requestDto.categories()) {
            allCategories.add(RestaurantCategoryMapper.toRestaurantCategory(restaurant, category));
        }
        restaurantCategoryRepository.saveAll(allCategories);
    }

    // 식당 수정
    @Transactional
    public void editRestaurant(User user, RestaurantRequestDto requestDto, UUID restaurantId, List<MultipartFile> restaurantImage) {
//        validateUser(user);
        Restaurant findRestaurant = restaurantRepository.findByIdAndOwnerIdAndDeletedAtIsNull(restaurantId, user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 해당 식당 외 동일한 사업자 번호 검증
        if (restaurantRepository.existsByBusinessNumberAndIdNot(requestDto.businessNumber(), restaurantId)) {
            throw new BusinessException(ErrorCode.BUSINESS_CODE_EXISTS);
        }

        // Todo : 이미지 수정
        findRestaurant.editRestaurant(requestDto);

        // 기존 식당_카테고리 삭제후 재생성
        restaurantCategoryRepository.deleteAllByRestaurant(findRestaurant);
        ArrayList<RestaurantCategory> allCategories = new ArrayList<>();

        for (UUID category : requestDto.categories()) {
            allCategories.add(RestaurantCategoryMapper.toRestaurantCategory(findRestaurant, category));
        }
        restaurantCategoryRepository.saveAll(allCategories);
    }

    // 식당 삭제
    @Transactional
    public void deleteRestaurant(User user, UUID restaurantId) {
//        validateUser(user);
        Restaurant findRestaurant = restaurantRepository.findByIdAndOwnerIdAndDeletedAtIsNull(restaurantId, user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        findRestaurant.delete(user.getId());
    }

    // 식당 조회 (사장)
    @Transactional(readOnly = true)
    public List<RestaurantDetailResponseDto> getRestaurants(User user) {
//        validateUser(user);
        List<Restaurant> OwnerRestaurantList = restaurantRepository.findAllByOwnerIdAndDeletedAtIsNull(user.getId());

        List<RestaurantDetailResponseDto> restaurantList = new ArrayList<>();
        if (!OwnerRestaurantList.isEmpty()) {
            restaurantList = OwnerRestaurantList.stream()
                    .map(RestaurantMapper::toRestaurantDetailResponseDto)
                    .toList();
        }
        return restaurantList;
    }

    // 리스트 조회 (필터 처리 :  1.거리순 2.별점순 3.이름 4.카테고리)
    @Transactional(readOnly = true)
    public SliceListResponseDto getAllRestaurants(int page, int size, String sortBy, UUID category, String name, Double lat, Double lon) {
        Slice<RestaurantListResponseDto> restaurantList;
        // 거리 기준 정렬
        if ("distance".equalsIgnoreCase(sortBy)) {
            // Bounding Box 계산 로직 (3km 반경내 식당 필터 후 거리 계산)
            double distance = 3.0; // 3km 반경

            double R = 6371; // 지구 반지름(km)

            // 위도, 경도 delta 계산 (3km 크기 위도, 경도 값)
            double latDelta = Math.toDegrees(distance / R);
            double lonDelta = Math.toDegrees(distance / (R * Math.cos(Math.toRadians(lat))));

            // 경계값 계산 (3km 반경 위도, 경도 값)
            double minLat = lat - latDelta;
            double maxLat = lat + latDelta;
            double minLon = lon - lonDelta;
            double maxLon = lon + lonDelta;

            Pageable pageable = PageRequest.of(page, size);
            restaurantList = restaurantRepository.findNearbyWithBoundingBox(
                    name, category, lat, lon, minLat, maxLat, minLon, maxLon, pageable
            );

            // 별점 정렬 (식당에 존재하는 필드중 다른 정렬 조건 입력가능)
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            restaurantList = restaurantRepository.findWithFilters(name, category, lat, lon, pageable);
        }

        return RestaurantMapper.toSliceListResponseDto(restaurantList.getContent(), restaurantList.getNumber(), restaurantList.hasNext());
    }

    // 상세 정보 조회
    @Transactional(readOnly = true)
    public RestaurantDetailResponseDto getRestaurantDetail(UUID restaurantId) {
        Restaurant findRestaurant = restaurantRepository.findByIdAndDeletedAtIsNull(restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        return RestaurantMapper.toRestaurantDetailResponseDto(findRestaurant);
    }

    // 별점 반영
    @Transactional
    public void editRating(UUID restaurantId, RatingRequestDto requestDto) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(restaurantId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        // review count, total rating 생성해 계산 -> 리뷰 삭제(DELETE), 생성(UPDATE) 고려
        if (requestDto.status().equals(RatingStatus.UPDATE)) {
            restaurant.updateRating(requestDto.rating());
        } else {
            restaurant.deleteRating(requestDto.rating());
        }
    }

    // 사용자 권한 검증 (Owner 사용자만 CUD + R(사장조회) 가능)
    private void validateUser(User user) {
        if (!user.getRole().equals(Role.OWNER)) {
            throw new BusinessException(ErrorCode.ROLE_AUTHORIZATION_REQUIRED);
        }
    }

}
