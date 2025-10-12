package com.sparta.delivery.menu.service;

import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.domain.MenuStatus;
import com.sparta.delivery.menu.dto.MenuCreateRequest;
import com.sparta.delivery.menu.dto.MenuResponse;
import com.sparta.delivery.menu.dto.MenuUpdateRequest;
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

    @PersistenceContext
    private EntityManager em;

    public MenuResponse create(MenuCreateRequest req) {
        // 식당 프록시 참조 (식당을 진짜로 불러오지 않고 ID로만 연결)
        Restaurant restaurantRef = em.getReference(Restaurant.class, req.restaurantId());

        Menu menu = Menu.builder()
                .restaurant(restaurantRef)
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .discountPrice(req.discountPrice() != null ? req.discountPrice() : 0)
                .option(req.option())
                .status(req.status() != null ? req.status() : MenuStatus.SALE)
                .build();

        Menu saved = menuRepository.save(menu);
        return toResponse(saved);
    }

    public MenuResponse get(UUID menuId) {
        return toResponse(find(menuId));
    }

    public List<MenuResponse> listByRestaurant(UUID restaurantId) {
        return menuRepository.findByRestaurant_Id(restaurantId)
                .stream().map(this::toResponse).toList();
    }

    public MenuResponse update(UUID menuId, MenuUpdateRequest req) {
        Menu menu = find(menuId);
        menu.update(req.name(), req.description(), req.price(),
                req.discountPrice(), req.option(), req.status());
        return toResponse(menu); // dirty checking으로 반영
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

    private MenuResponse toResponse(Menu m) {
        return new MenuResponse(
                m.getId(),
                m.getRestaurant().getId(),
                m.getName(),
                m.getDescription(),
                m.getPrice(),
                m.getDiscountPrice(),
                m.getOption(),
                m.getStatus(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}