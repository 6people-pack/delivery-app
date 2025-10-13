package com.sparta.delivery.cartitem.controller;

import com.sparta.delivery.cartitem.dto.AddCartItemDto;
import com.sparta.delivery.cartitem.dto.GetCartItemDto;
import com.sparta.delivery.cartitem.dto.updateCartItemOptionsDto;
import com.sparta.delivery.cartitem.dto.updateCartItemQuantityDto;
import com.sparta.delivery.cartitem.service.CartItemService;
import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cartitems")
@AllArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    // 예시 입니다 참고용
//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("/inquiry")
//    public BaseResponse<PreSignedUrlListResponse> newInquiry(@AuthenticationPrincipal PrincipalUser principal, @RequestBody @Valid InquiryCreateRequest request) {
//        return BaseResponse.ok(inquiryFacade.createNewInquiry(principal.getId(), request), BaseStatus.OK);
//    }

    // 장바구니 메뉴 추가
    @PostMapping("/")
    public BaseResponse<Void> addCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid AddCartItemDto dto) {

        cartItemService.addCartItem(userDetails.getUser(), dto);

        return BaseResponse.ok(BaseStatus.CREATED);
    }

    // 장바구니 메뉴 조회
    @GetMapping("/")
    public BaseResponse<List<GetCartItemDto>> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return BaseResponse.ok(cartItemService.getCartItems(userDetails.getUser()), BaseStatus.OK);
    }

    // 장바구니 메뉴 수량 변경
    @PatchMapping("/quantity")
    public BaseResponse<Void> updateCartItemQuantity(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid updateCartItemQuantityDto dto) {

        cartItemService.updateCartItemQuantity(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 장바구니 메뉴 삭제
    @DeleteMapping("/{cartItemId}")
    public BaseResponse<Void> deleteCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("cartItemId") UUID cartItemId) {

        cartItemService.deleteCartItem(userDetails.getUser(), cartItemId);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 메뉴 옵션 변경
    @PatchMapping("/options")
    public BaseResponse<Void> updateCartItemOption(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid updateCartItemOptionsDto dto) {

        cartItemService.updateCartItemOption(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

}
