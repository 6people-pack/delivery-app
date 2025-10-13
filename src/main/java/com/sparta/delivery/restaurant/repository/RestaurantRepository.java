package com.sparta.delivery.restaurant.repository;

import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.dto.RestaurantListResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    boolean existsByBusinessNumber(String businessNumber);

    List<Restaurant> findAllByOwnerIdAndDeletedAtIsNull(Long ownerId);

    Optional<Restaurant> findByIdAndDeletedAtIsNull(UUID restaurantId);

    Optional<Restaurant> findByIdAndOwnerIdAndDeletedAtIsNull(UUID restaurantId, Long userId);

    boolean existsByBusinessNumberAndIdNot(String businessNumber, UUID id);

    // 별점 또는 다른 기준으로 정렬
    @Query(value = "SELECT " +
            "r.restaurant_id AS restaurantId, " +
            "r.name AS name, " +
            "r.min_order_price AS minOrderPrice, " +
            "r.is_open AS isOpen, " +
            "r.rating AS rating, " +
            "(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(r.latitude)) * COS(RADIANS(r.longitude) - RADIANS(:lon)) + SIN(RADIANS(:lat)) * SIN(RADIANS(r.latitude)))) AS distance " +
            "FROM restaurant r " +
            "JOIN restaurant_category rc ON r.restaurant_id = rc.restaurant_id " +
            "WHERE r.deleted_at IS NULL " +
            "AND (:name IS NULL OR r.name LIKE CONCAT('%', :name, '%')) " +
            "AND (:category IS NULL OR rc.category_id = CAST(:category AS uuid))", // UUID 타입 캐스팅
            nativeQuery = true)
    Slice<RestaurantListResponseDto> findWithFilters(@Param("name") String name,
                                                  @Param("category") UUID category,
                                                  @Param("lat") Double lat,
                                                  @Param("lon") Double lon,
                                                  Pageable pageable);

    // 거리 기준 정렬 (Bounding Box 사용)
    @Query(value = "SELECT " +
            "r.restaurant_id AS restaurantId, " +
            "r.name AS name, " +
            "r.min_order_price AS minOrderPrice, " +
            "r.is_open AS isOpen, " +
            "r.rating AS rating, " +
            "(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(r.latitude)) * COS(RADIANS(r.longitude) - RADIANS(:lon)) + SIN(RADIANS(:lat)) * SIN(RADIANS(r.latitude)))) AS distance " +
            "FROM restaurant r " +
            "LEFT JOIN restaurant_category rc ON r.restaurant_id = rc.restaurant_id " +
            "WHERE r.deleted_at IS NULL " +
            // 3km 필터 적용 (1차)
            "AND r.latitude BETWEEN :minLat AND :maxLat " +
            "AND r.longitude BETWEEN :minLon AND :maxLon " +
            "AND (:name IS NULL OR r.name LIKE CONCAT('%', :name, '%')) " +
            "AND (:category IS NULL OR rc.category_id = CAST(:category AS uuid)) " +
            // 3km 거리 계산 및 필터링 (2차)
            "AND (6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(r.latitude)) * COS(RADIANS(r.longitude) - RADIANS(:lon)) + SIN(RADIANS(:lat)) * SIN(RADIANS(r.latitude)))) <= 3.0 " +
            "ORDER BY distance ASC",
            nativeQuery = true)
    Slice<RestaurantListResponseDto> findNearbyWithBoundingBox(@Param("name") String name,
                                                            @Param("category") UUID category,
                                                            @Param("lat") Double lat,
                                                            @Param("lon") Double lon,
                                                            @Param("minLat") Double minLat,
                                                            @Param("maxLat") Double maxLat,
                                                            @Param("minLon") Double minLon,
                                                            @Param("maxLon") Double maxLon,
                                                            Pageable pageable);
}