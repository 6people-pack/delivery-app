package com.sparta.delivery.order.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.order.dto.CancelOrderOwnerRequestDto;
import com.sparta.delivery.order.dto.GetOrderDetailResponseDto;
import com.sparta.delivery.order.dto.GetOrderResponseDto;
import com.sparta.delivery.order.dto.CreateOrderRequestDto;
import com.sparta.delivery.order.service.OrderService;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping("/")
    public BaseResponse<Void> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CreateOrderRequestDto dto) {

        orderService.createOrder(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.CREATED);
    }

    // 전체 주문 조회(고객)
    @GetMapping("/")
    public BaseResponse<List<GetOrderResponseDto>> getOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return BaseResponse.ok(orderService.getOrders(userDetails.getUser()), BaseStatus.OK);
    }

    // 단일 주문 상세 조회
    @GetMapping("/{orderId}")
    public BaseResponse<GetOrderDetailResponseDto> getOrdersDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("orderId") UUID orderId) {

        return BaseResponse.ok(orderService.getOrdersDetail(userDetails.getUser(), orderId), BaseStatus.OK);
    }

    // 가게 주문 현황 조회(점주)
    // 개선 사항 페이징 처리
    @GetMapping("/owner/{restaurantId}")
    public BaseResponse<List<GetOrderDetailResponseDto>> getOrdersOwner(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("restaurantId") UUID restaurantId) {

        return BaseResponse.ok(orderService.getOrdersOwner(userDetails.getUser(), restaurantId), BaseStatus.OK);
    }

    // 주문 취소(고객), 수락 전이라면 취소 가능
    @PatchMapping("/{orderId}/cancel")
    public BaseResponse<Void> cancelOrderCustomer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("orderId") UUID orderId) {

        orderService.cancelOrderCustomer(userDetails.getUser(),orderId);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 주문 취소(점주), 수락 후 취소 시, 메세지 동반
    @PatchMapping("/owner/cancel")
    public BaseResponse<Void> cancelOrderOwner(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid CancelOrderOwnerRequestDto dto) {

        orderService.cancelOrderOwner(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 주문 상태 변경(점주), 주문 요청에 대해 수락, 배달, 완료 (배달하기 요청을 보내고 바로 완료 요청이 왔다고 가정하고 완료 처리)
    @PatchMapping("/owner/{orderId}/status")
    public BaseResponse<Void> updateOrderStatus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("orderId") UUID orderId) {

        orderService.updateOrderStatus(userDetails.getUser(),orderId);
        return BaseResponse.ok(BaseStatus.OK);
    }
}
