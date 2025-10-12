package com.sparta.delivery.menu.repository;

import com.sparta.delivery.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    /**
     * 특정 식당(UUID) 기준으로 등록된 모든 메뉴 조회
     */
    List<Menu> findByRestaurant_Id(UUID restaurantId);

    /**
     * 특정 식당 내에서 이름이 같은 메뉴가 이미 존재하는지 확인
     */
    boolean existsByRestaurant_IdAndName(UUID restaurantId, String name);
}