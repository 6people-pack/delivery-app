package com.sparta.delivery.menu.service;

import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.dto.MenuCreateRequestDto;
import com.sparta.delivery.menu.dto.MenuUpdateRequestDto;
import com.sparta.delivery.menu.dto.MenuResponseDto;
import com.sparta.delivery.menu.dto.MenuSummaryDto;
import com.sparta.delivery.menu.mapper.MenuMapper;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.domain.Restaurant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    @PersistenceContext
    private EntityManager em;

    public MenuResponseDto create(MenuCreateRequestDto req) {
        // 식당 프록시 참조 (ID만으로 참조)
        Restaurant restaurantRef = em.getReference(Restaurant.class, req.restaurantId());

        Menu menu = menuMapper.toEntity(req, restaurantRef);
        Menu saved = menuRepository.save(menu);
        return menuMapper.toResponse(saved);
    }

    public MenuResponseDto get(UUID menuId) {
        return menuMapper.toResponse(find(menuId));
    }

    //요약 DTO로 가볍게 내려줌
    public List<MenuSummaryDto> listByRestaurant(UUID restaurantId) {
        return menuRepository.findByRestaurant_Id(restaurantId)
                .stream()
                .map(menuMapper::toSummary)
                .toList();
    }

    public MenuResponseDto update(UUID menuId, MenuUpdateRequestDto req) {
        Menu menu = find(menuId);
        // 엔티티의 부분 업데이트 메서드 활용 (null만 건너뜀)
        menu.update(req.name(), req.description(), req.price(),
                req.discountPrice(), req.option(), req.status());
        return menuMapper.toResponse(menu); // dirty checking 반영
    }

    public void delete(UUID menuId) {
        if (!menuRepository.existsById(menuId)) {
            throw new IllegalArgumentException("Menu not found: " + menuId);
        }
        menuRepository.deleteById(menuId);
    }

    private Menu find(UUID id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + id));
    }
}