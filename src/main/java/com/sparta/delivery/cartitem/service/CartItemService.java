package com.sparta.delivery.cartitem.service;

import com.sparta.delivery.cartitem.domain.CartItem;
import com.sparta.delivery.cartitem.dto.AddCartItemRequestDto;
import com.sparta.delivery.cartitem.dto.GetCartItemResponseDto;
import com.sparta.delivery.cartitem.dto.updateCartItemOptionRequestDto;
import com.sparta.delivery.cartitem.dto.updateCartItemQuantityRequestDto;
import com.sparta.delivery.cartitem.repository.CartItemRepository;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.domain.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    // 장바구니 메뉴 추가
    @Transactional
    public void addCartItem(User user, @Valid AddCartItemRequestDto dto) {
        List<CartItem> findCartItems = cartItemRepository.findByUserId(user.getId());
        // 장바구니의 첫번째 메뉴 확인, 비었으면 null
        CartItem firstItem = findCartItems.stream().findFirst().orElse(null);

        Menu menu = menuRepository.findById(dto.menuId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        // 실제 존재하는 식당인지 확인
        if (!restaurantRepository.existsById(dto.restaurantId())) throw new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND);

        // 디폴트나 세일 상태가 아닌지 확인
        if (!EnumSet.of(MenuStatus.DEFAULT, MenuStatus.SALE).contains(menu.getStatus())) {
            throw new BusinessException(ErrorCode.INVALID_MENU_STATUS);
        }

        // 장바구니에 메뉴가 있고 그 메뉴가 추가할 메뉴와 같은 음식점이 아닐 때
        if (firstItem != null && !menu.getRestaurantId().equals(firstItem.getRestaurantId())) {
            if (dto.override() == null) { // 첫번째 요청일 때 null
                throw new BusinessException(ErrorCode.DIFFERENT_RESTAURANT); // 재요청
            }
            if (dto.override()) {   // 재요청 후 덮어쓰기 확인 시
                findCartItems.forEach(item -> item.delete(user.getId()));
            } else if (!dto.override()) {
                return;
            }
        }

        cartItemRepository.save(CartItem.createCartItem(
                user.getId(),
                dto.menuId(),
                dto.restaurantId(),
                dto.option(),
                dto.quantity()
        ));

    }

    // 장바구니 상품 조회
    @Transactional(readOnly = true)
    public List<GetCartItemResponseDto> getCartItems(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        // 비었으면 빈 리스트 리턴
        return cartItems.stream().map(item -> new GetCartItemResponseDto(
                item.getMenuId(),
                item.getOption(),
                item.getQuantity()
        )).toList();
    }

    // 장바구니 메뉴 수량 변경
    @Transactional
    public void updateCartItemQuantity(User user, @Valid updateCartItemQuantityRequestDto dto) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(dto.cartItemId(), user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND)); // 선택한 메뉴가 db에 없을 때

        if (dto.increase()) {
            cartItem.increaseQuantity();
        } else {
            cartItem.decreaseQuantity();
        }

        cartItemRepository.save(cartItem);

    }

    // 장바구니 상품 삭제
    @Transactional
    public void deleteCartItem(User user, UUID cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND)); // 삭제 상품이 없을 때, 임시

        cartItem.delete(user.getId());
        cartItemRepository.save(cartItem);
    }

    // 메뉴 옵션 변경
    @Transactional
    public void updateCartItemOption(User user, @Valid updateCartItemOptionRequestDto dto) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(dto.cartItemId(), user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.updateOptions(dto.option());
        cartItemRepository.save(cartItem);
    }

}