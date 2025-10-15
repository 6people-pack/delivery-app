package com.sparta.delivery.order.service;

import com.sparta.delivery.cartitem.domain.CartItem;
import com.sparta.delivery.cartitem.repository.CartItemRepository;
import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.domain.MenuStatus;
import com.sparta.delivery.menu.repository.MenuRepository;
import com.sparta.delivery.order.domain.Order;
import com.sparta.delivery.order.domain.OrderItem;
import com.sparta.delivery.order.domain.OrderStatus;
import com.sparta.delivery.order.dto.*;
import com.sparta.delivery.order.repository.OrderItemRepository;
import com.sparta.delivery.order.repository.OrderRepository;
import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.user.domain.Role;
import com.sparta.delivery.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;

    // 주문 생성
    @Transactional
    public void createOrder(User user, @Valid CreateOrderRequestDto dto) {
        int menuPriceSum = 0;
        int menuDiscountPriceSum = 0;

        // 주문 상품
        // 사용자 장바구니 아이템 조회
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        // 장바구니가 비어 있을 때 에러
        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        // 식당id 저장
        UUID restaurantId = cartItems.get(0).getRestaurantId();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Menu menu = menuRepository.findById(cartItem.getMenuId()).orElseThrow(
                    () -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

            // 디폴트나 세일 상태가 아닌지 확인
            if (!EnumSet.of(MenuStatus.DEFAULT, MenuStatus.SALE).contains(menu.getStatus())) {
                throw new BusinessException(ErrorCode.INVALID_MENU_STATUS);
            }

            menuPriceSum += menu.getPrice() * cartItem.getQuantity();
            menuDiscountPriceSum += menu.getDiscountPrice() * cartItem.getQuantity();

            orderItems.add(OrderItem.create(
                    menu.getName(),
                    menu.getPrice(),
                    menu.getDiscountPrice(),
                    cartItem.getQuantity(),
                    cartItem.getOption(),
                    menu.getId()
            ));
        }

        // 배달비
        // Todo : 배달비 여유되면 계산 로직 추가
        int deliveryFee = 0;    // 일단 무료배달

        int totalAmount = menuPriceSum - menuDiscountPriceSum + deliveryFee;
        int vat = totalAmount / 10;
        String orderNumber = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime orderedAt = LocalDateTime.now();

        orderRepository.save(Order.create(
                orderNumber,
                dto.address(),
                dto.addressDetail(),
                user.getId(),
                restaurantId,
                menuPriceSum,
                vat,
                deliveryFee,
                menuDiscountPriceSum,
                totalAmount,
                dto.customerRequest(),
                orderItems,
                orderedAt
        ));
        orderItemRepository.saveAll(orderItems);
        // 주문 생성 후 장바구니 비우기
        cartItems.forEach(item -> item.delete(user.getId()));
        cartItemRepository.saveAll(cartItems);

    }

    // 주문 조회
    @Transactional(readOnly = true)
    public List<GetOrderResponseDto> getOrders(User user) {

        // 사용자 ID로 DB에서 주문 목록 가져오기
        List<Order> orders = orderRepository.findByUserId(user.getId());

        // 주문 하나씩 DTO로 변환
        List<GetOrderResponseDto> responseDtos = orders.stream().map(order -> {

            // 주문에 포함된 상품들을 DTO로 변환
            List<GetOrderItemResponseDto> orderItemDtos = order.getOrderItems().stream().map(orderItem ->
                    new GetOrderItemResponseDto(
                            orderItem.getMenuName(),
                            orderItem.getQuantity(),
                            orderItem.getOption()
                    )
            ).toList();

            // 주문 정보를 DTO로 변환
            return new GetOrderResponseDto(
                    order.getOrderedAt(),      // 주문 시각
                    order.getOrderStatus(),    // 주문 상태
                    order.getRestaurantId(),   // 음식점 ID, 식당 이름이었으면 더 좋았을 듯
                    order.getTotalAmount(),    // 총 금액
                    orderItemDtos              // 변환된 주문 상품 리스트
            );

        }).toList(); // 모든 주문 DTO를 리스트로 변환

        return responseDtos;

//        List<GetOrderResponseDto> responseDtos = new ArrayList<>(); // 반환할 주문dto 리스트 형태
//        List<Order> orders = orderRepository.findByUserId(user.getId()); // 먼저 db에 있는 주문들 정보 가져옴
//
//        for  (Order order : orders) { // 주문별 처리 로직
//            List<GetOrderItemResponseDto> orderItemDtos = new ArrayList<>(); // 주문 아이템 dto 리스트를 생성
//
//            List<OrderItem> orderItems = order.getOrderItems();         // db에서 주문에 해당하는 상품 가져옴
//            for(OrderItem orderItem : orderItems) {                 // db에 있는 주문 상품을 dto로 변환하고 리스트에 담음
//                orderItemDtos.add(new GetOrderItemResponseDto(
//                        orderItem.getMenuName(),
//                        orderItem.getQuantity(),
//                        orderItem.getOption()
//                ));
//            }
//
//            responseDtos.add(new GetOrderResponseDto(                     // db의 주문 정보를 dto로 변환 후 리스트에 담음
//                    order.getOrderedAt(),
//                    order.getOrderStatus(),
//                    order.getRestaurantId(),
//                    order.getTotalAmount(),
//                    orderItemDtos
//            ));
//        }
//
//        return responseDtos;
    }

    // 주문 상세(조회)
    public GetOrderDetailResponseDto getOrdersDetail(User user, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 주문의 사용자 id와 현재 인증된 사용자 id 비교
        if (!order.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 주문 항목 조회 및 DTO 변환
        List<GetOrderItemDetailResponseDto> orderItemDetailDtos = order.getOrderItems().stream()
                .map(item -> new GetOrderItemDetailResponseDto(
                        item.getMenuName(),
                        item.getMenuPrice(),
                        item.getMenuDiscountPrice(),
                        item.getQuantity(),
                        item.getOption()
                )).toList();


        return new GetOrderDetailResponseDto(
                order.getOrderNumber(),
                order.getOrderStatus(),
                order.getAddress(),
                order.getAddressDetail(),
                order.getGrossAmount(),
                order.getDeliveryFee(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getCustomerRequest(),
                order.getOrderedAt(),
                order.getAcceptedAt(),
                order.getDeliveringAt(),
                order.getDeliveredAt(),
                order.getCanceledAt(),
                order.getCancelMessage(),
                orderItemDetailDtos
        );

    }

    // 가게 주문 현황 조회(점주)
    public List<GetOrderDetailResponseDto> getOrdersOwner(User user, UUID restaurantId) {
        Restaurant findRestaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        //식당의 대표자와 유저의 id가 맞는지
        if (!findRestaurant.getOwnerId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 해당 식당의 주문들 조회
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);

        List<GetOrderDetailResponseDto> responseDtos = orders.stream().map(order -> {

            List<GetOrderItemDetailResponseDto> orderItemDetailDtos = order.getOrderItems().stream().map(orderItem ->
                    new GetOrderItemDetailResponseDto(
                            orderItem.getMenuName(),
                            orderItem.getMenuPrice(),
                            orderItem.getMenuDiscountPrice(),
                            orderItem.getQuantity(),
                            orderItem.getOption()
                    )
            ).toList();

            return new GetOrderDetailResponseDto(
                    order.getOrderNumber(),
                    order.getOrderStatus(),
                    order.getAddress(),
                    order.getAddressDetail(),
                    order.getGrossAmount(),
                    order.getDeliveryFee(),
                    order.getDiscountAmount(),
                    order.getTotalAmount(),
                    order.getCustomerRequest(),
                    order.getOrderedAt(),
                    order.getAcceptedAt(),
                    order.getDeliveringAt(),
                    order.getDeliveredAt(),
                    order.getCanceledAt(),
                    order.getCancelMessage(),
                    orderItemDetailDtos
            );
        }).toList();

        return responseDtos;

    }

    // 주문 취소(고객)
    @Transactional
    public void cancelOrderCustomer(User user, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 관리자 or 주문에 맞는 유저인지 검증
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOrder = user.getRole() == Role.CUSTOMER && order.getUserId().equals(user.getId());

        if (!isAdmin && !isOrder) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_ACCESS);
        }
        if (order.getOrderStatus() == OrderStatus.REQUESTED) {
            order.changeStatusCanceled();
            orderRepository.save(order);
        } else {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }
    }

    // 주문 취소(점주)
    @Transactional
    public void cancelOrderOwner(User user, @Valid CancelOrderOwnerRequestDto dto) {
        Order order = orderRepository.findById(dto.orderId()).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 추가 검증 필요, 주문이 사장의 가게 주문인지
        if (!(user.getRole().name().equals("ADMIN") || (user.getRole().name().equals("OWNER")))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        order.changeStatusCanceled(dto.cancelMessage());
        orderRepository.save(order);
    }

    // 주문 상태 변경(점주)
    @Transactional
    public void updateOrderStatus(User user, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 추가 검증 필요, 주문이 사장의 가게 주문인지
        if (!(user.getRole().name().equals("ADMIN") || (user.getRole().name().equals("OWNER")))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (order.getOrderStatus() == OrderStatus.REQUESTED) {
            order.changeStatusAccepted();
        }
        else if (order.getOrderStatus() == OrderStatus.ACCEPTED) {
            order.changeStatusDelivering();

            //바로 배달 완료 처리
            order.changeStatusDelivered();
        }

        orderRepository.save(order);
    }


}
