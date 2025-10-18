package com.sparta.delivery.menu.service;

import com.sparta.delivery.global.category.ImageCategory;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.image.service.ImageService;
import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.dto.MenuCreateRequestDto;
import com.sparta.delivery.menu.dto.MenuResponseDto;
import com.sparta.delivery.menu.dto.MenuUpdateRequestDto;
import com.sparta.delivery.menu.mapper.MenuMapper;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;
    private final ImageService imageService;
    private final RestaurantRepository restaurantRepository;

    @PersistenceContext
    private EntityManager em;

    public MenuResponseDto create(MenuCreateRequestDto req, User user, List<MultipartFile> menuImages) {
        // 식당 프록시 참조 (ID만으로 참조)
//        Restaurant restaurantRef = em.getReference(Restaurant.class, req.restaurantId());

        // 해당 사용자가 식당 주인인지 검증
        if (!restaurantRepository.existsByIdAndOwnerIdAndDeletedAtIsNull(req.restaurantId(), user.getId())) throw new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND);

        Menu menu = menuMapper.toEntity(req, req.restaurantId());
        Menu saved = menuRepository.save(menu);
        imageService.uploadImage(ImageCategory.menu, saved.getId(), menuImages);

        return menuMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MenuResponseDto get(UUID menuId) {
        return menuMapper.toResponse(find(menuId));
    }

    // 요약 DTO로 가볍게 내려주려면 mapper에서 요약 변환 메서드 따로 쓰면 됨
    @Transactional(readOnly = true)
    public List<MenuResponseDto> listByRestaurant(UUID restaurantId) {
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId); // ⬇️ 2번
        return menus.stream().map(menuMapper::toResponse).toList();
    }

    public MenuResponseDto update(UUID menuId, MenuUpdateRequestDto req) {
        Menu menu = find(menuId);
        // 엔티티의 부분 업데이트 메서드 활용 (null만 건너뜀)
        menu.update(
                req.name(),
                req.description(),
                req.price(),
                req.discountPrice(),
                req.option(),
                req.status()
        );
        // 영속 상태이므로 flush 시점에 dirty checking 반영
        return menuMapper.toResponse(menu);
    }

    public void delete(User user, UUID menuId) {
        Menu menu = find(menuId);
        menu.delete(user.getId()); // Soft Delete 적용
        imageService.deleteImageFolder(ImageCategory.menu, menuId);
    }

    private Menu find(UUID id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + id));
    }
}