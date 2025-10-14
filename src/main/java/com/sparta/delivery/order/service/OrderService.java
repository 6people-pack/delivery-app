package com.sparta.delivery.order.service;

import com.sparta.delivery.cartitem.dto.GetCartItemDto;
import com.sparta.delivery.cartitem.service.CartItemService;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.order.domain.Order;
import com.sparta.delivery.order.domain.OrderItem;
import com.sparta.delivery.order.domain.OrderStatus;
import com.sparta.delivery.order.dto.CancelOrderOwnerDto;
import com.sparta.delivery.order.dto.GetOrderDto;
import com.sparta.delivery.order.dto.GetOrderItemDto;
import com.sparta.delivery.order.dto.CreateOrderDto;
import com.sparta.delivery.order.repository.OrderRepository;
import com.sparta.delivery.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemService cartItemService;

    // 주문 생성
    public void createOrder(User user, @Valid CreateOrderDto dto) {
        String orderNumber = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String address = dto.address();                 // 주소
        String addressDetail = dto.addressDetail();     // 상세주소
        Long userId = user.getId();
        int originalPrice = 0;
        int discountAmount = 0;
        LocalDateTime orderedAt = LocalDateTime.now();

        // 배송비
        int deliveryFee = 0;    // 일단 무료배송

        String customerRequest = dto.customerRequest(); // 요청사항

        // 주문 상품
        List<OrderItem> orderItems = new ArrayList<>();
        List<GetCartItemDto> cartItems = cartItemService.getCartItems(user);
//        UUID restaurantId = cartItems.get(0).menu().getRestaurantId();  // 식당id
//
//        for (GetCartItemDto item : cartItems) {
//            originalPrice += item.menu().getPrice();
//            discountAmount += item.menu().getDiscountPrice();
//
//            orderItems.add(new OrderItem(
//                    item.menu().getName(),
//                    item.menu().getPrice(),
//                    item.quantity(),
//                    item.options(),
//                    item.menu().getId()
//            ));
//        }
//
//        // 주문 가격
//        int totalAmount = originalPrice - discountAmount + deliveryFee;
//        int vat = totalAmount / 10;
//
//        orderRepository.save(new Order(
//                orderNumber,
//                address,
//                addressDetail,
//                userId,
//                restaurantId,
//                originalPrice,
//                vat,
//                deliveryFee,
//                discountAmount,
//                totalAmount,
//                customerRequest,
//                orderItems,
//                orderedAt
//
//        ));

    }

    // 주문 조회
    public List<GetOrderDto> getOrders(User user) {

        List<GetOrderDto> orderDtoList = new ArrayList<>(); // 반환할 주문dto 리스트 형태
        List<Order> orders = orderRepository.findByUserId(user.getId()); // 먼저 db에 있는 주문들 정보 가져옴

        for  (Order order : orders) { // 주문별 처리 로직
            List<GetOrderItemDto> orderItemDtoList = new ArrayList<>(); // 주문 아이템 dto 리스트를 생성

            List<OrderItem> orderItems = order.getOrderItems();         // db에서 주문에 해당하는 상품 가져옴
            for(OrderItem orderItem : orderItems) {                 // db에 있는 주문 상품을 dto로 변환하고 리스트에 담음
                orderItemDtoList.add(new GetOrderItemDto(
                        orderItem.getMenuName(),
                        orderItem.getQuantity(),
                        orderItem.getOption()
                ));
            }

            orderDtoList.add(new GetOrderDto(                     // db의 주문 정보를 dto로 변환 후 리스트에 담음
                    order.getOrderedAt(),
                    order.getOrderStatus(),
                    order.getRestaurantId(), // 임시, 일단 기본키 저장
                    order.getGrossAmount(),
                    order.getTotalAmount(),
                    orderItemDtoList
            ));
        }

        return orderDtoList;
    }

    // 주문 취소 고객
    public void cancelOrderCustomer(User user, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 관리자 or 주문에 맞는 유저인지 검증
        if (!(user.getRole().name().equals("ADMIN") || (user.getRole().name().equals("USER") && order.getUserId().equals(user.getId())))) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (order.getOrderStatus() == OrderStatus.REQUESTED) {
            order.changeStatusCanceled();
            orderRepository.save(order);
        } else {
            throw new BusinessException(ErrorCode.FORBIDDEN);

        }

    }

    // 주문 취소 점주
    public void cancelOrderOwner(User user, @Valid CancelOrderOwnerDto dto) {
        Order order = orderRepository.findById(dto.orderId()).orElseThrow(()-> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 추가 검증 필요, 주문이 사장의 가게 주문인지
        if (!(user.getRole().name().equals("ADMIN") || (user.getRole().name().equals("OWNER") ))) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        order.changeStatusCanceled(dto.cancelMessage());
        orderRepository.save(order);
    }

    // 주문 상태 변경 점주
    public void updateOrderStatus(User user, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 추가 검증 필요, 주문이 사장의 가게 주문인지
        if (!(user.getRole().name().equals("ADMIN") || (user.getRole().name().equals("OWNER") ))) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (order.getOrderStatus() == OrderStatus.REQUESTED) {
            order.changeStatusAccepted();
        }
        if (order.getOrderStatus() == OrderStatus.ACCEPTED) {
            order.changeStatusDelivering();

            //바로 배달 완료 처리
            order.changeStatusDelivered();
        }

        orderRepository.save(order);
    }

}
