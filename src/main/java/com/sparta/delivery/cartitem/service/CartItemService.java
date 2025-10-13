package com.sparta.delivery.cartitem.service;

import com.sparta.delivery.cartitem.domain.CartItem;
import com.sparta.delivery.cartitem.dto.AddCartItemDto;
import com.sparta.delivery.cartitem.dto.GetCartItemDto;
import com.sparta.delivery.cartitem.dto.updateCartItemOptionsDto;
import com.sparta.delivery.cartitem.dto.updateCartItemQuantityDto;
import com.sparta.delivery.cartitem.repository.CartItemRepository;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.user.domain.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    // 관리자도 아니고 자신이 쓴 문의글이 아니면 조회 불가
//        if (!user.getRole().equals(Role.ADMIN) && !inquiry.getUser().getId().equals(user.getId())) {
//        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
//    }

    // 장바구니 메뉴 추가
    public void addCartItem(User user, AddCartItemDto addCartItemDto) {
        if (!addCartItemDto.menu().isStatus()){
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);  // 판매중이 아닐 때, 임시
        }

        // 여기 식당이 다를 때 기존 장바구니 삭제 후 상품 추가 로직 추가

        cartItemRepository.save(new CartItem(
                user,
                addCartItemDto.menu(),
                addCartItemDto.option(),
                addCartItemDto.quantity()
        ));
    }

    // 장바구니 상품 조회
    public List<GetCartItemDto> getCartItems(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return cartItems.stream().map(item -> new GetCartItemDto(
                item.getMenu(),
                item.getOptions(),
                item.getQuantity()
        )).toList();
    }

    // 장바구니 메뉴 수량 변경
    public void updateCartItemQuantity(User user, @Valid updateCartItemQuantityDto dto) {
        CartItem cartItem = cartItemRepository.findByIdAndUser(dto.cartItemId(), user)
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
        CartItem cartItem = cartItemRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS)); // 삭제 상품이 없을 때, 임시
        cartItemRepository.delete(cartItem);

    }

    // 메뉴 옵션 변경
    public void updateCartItemOption(User user, @Valid updateCartItemOptionsDto dto) {
        CartItem cartItem = cartItemRepository.findByIdAndUser(dto.cartItemId(), user)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS));
        cartItem.updateOptions(dto.options());
        cartItemRepository.save(cartItem);
    }
}
