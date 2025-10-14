package com.sparta.delivery.cartitem.service;

import com.sparta.delivery.cartitem.domain.CartItem;
import com.sparta.delivery.cartitem.dto.AddCartItemDto;
import com.sparta.delivery.cartitem.dto.GetCartItemDto;
import com.sparta.delivery.cartitem.dto.updateCartItemOptionDto;
import com.sparta.delivery.cartitem.dto.updateCartItemQuantityDto;
import com.sparta.delivery.cartitem.repository.CartItemRepository;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.user.domain.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    // 장바구니 메뉴 추가
    @Transactional
    public void addCartItem(User user, @Valid AddCartItemDto addCartItemDto) {
//        if (!addCartItemDto.menuId().isStatus()){
//            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);  // 판매중이 아닐 때, 임시
//        }

        cartItemRepository.save(CartItem.createCartItem(
                user.getId(),
                addCartItemDto.menuId(),
                addCartItemDto.option(),
                addCartItemDto.quantity()
        ));
    }

    // 장바구니 상품 조회
    public List<GetCartItemDto> getCartItems(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        return cartItems.stream().map(item -> new GetCartItemDto(
                item.getMenuId(),
                item.getOption(),
                item.getQuantity()
        )).toList();
    }

    // 장바구니 메뉴 수량 변경
    public void updateCartItemQuantity(User user, @Valid updateCartItemQuantityDto dto) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(dto.cartItemId(), user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS));

        if (dto.increase()) {
            cartItem.increaseQuantity();
        } else {
            cartItem.decreaseQuantity();
        }

        cartItemRepository.save(cartItem);

    }

    // 장바구니 상품 삭제
    public void deleteCartItem(User user, UUID cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS)); // 삭제 상품이 없을 때, 임시
        cartItemRepository.delete(cartItem);

    }

    // 메뉴 옵션 변경
    public void updateCartItemOption(User user, @Valid updateCartItemOptionDto dto) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(dto.cartItemId(), user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS));
        cartItem.updateOptions(dto.option());
        cartItemRepository.save(cartItem);
    }
}
