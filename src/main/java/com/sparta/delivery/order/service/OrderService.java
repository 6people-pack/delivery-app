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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderService {

    // 10분 뒤 주문 상태를 확인하기 위한 스케쥴러
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;

    // 주문 생성
    @Transactional
    public CreateOrderResponseDto createOrder(User user, CreateOrderRequestDto dto) {
        int menuPriceSum = 0;
        int menuDiscountPriceSum = 0;

        // TODO 결재 테스트 후 삭제
        // 유저의 가장 최근 주문 조회 후 결재 대기 상태면 그 주문의 아이디와 결재 금액 리턴
        // 결재페이지에서 주문 중복 생성을 막기 위한 임시 코드
        Optional<Order> recentOrderOpt = orderRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
        if (recentOrderOpt.isPresent()) {
            Order recentOrder = recentOrderOpt.get();
            if (recentOrder.getOrderStatus() == OrderStatus.PENDING) {
                return new CreateOrderResponseDto(recentOrder.getId(), recentOrder.getTotalAmount());
            }
        }

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

        Order newOrder = Order.create(
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
        );

        orderRepository.save(newOrder);
        orderItemRepository.saveAll(orderItems);
        // 주문 생성 후 장바구니 비우기
//        cartItems.forEach(item -> item.delete(user.getId()));
//        cartItemRepository.saveAll(cartItems);
// TODO 결제 테스트 후 주석 해제

        // 10분 뒤 아직도 결재 대기 상태면 취소 처리
        scheduler.schedule(() -> handlePendingOrder(newOrder.getId()), 10, TimeUnit.MINUTES);

        return new CreateOrderResponseDto(newOrder.getId(), totalAmount);
    }

    // 주문 조회
    @Transactional(readOnly = true)
    public Page<GetOrderResponseDto> getOrders(User user, Pageable pageable) {

        // 사용자 ID로 DB에서 주문 목록 가져오기
        Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable);

        // 주문 하나씩 DTO로 변환
        Page<GetOrderResponseDto> responseDtos = orders.map(order -> {

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

        }); // 모든 주문 DTO를 리스트로 변환

        return responseDtos;
    }

    // 주문 상세(조회)
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<GetOrderDetailResponseDto> getOrdersOwner(User user, UUID restaurantId, Pageable pageable) {
        Restaurant findRestaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

        //식당의 대표자와 유저의 id가 맞는지
        if (!findRestaurant.getOwnerId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 해당 식당의 주문들 조회
        Page<Order> orders = orderRepository.findByRestaurantId(restaurantId, pageable);

        Page<GetOrderDetailResponseDto> responseDtos = orders.map(order -> {
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
        });

        return responseDtos;

    }

    // 주문 취소(고객)
    @Transactional
    public void cancelOrderCustomer(User user, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 관리자 or 주문에 맞는 유저인지 검증
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOrder = user.getRole() == Role.CUSTOMER && order.getUserId().equals(user.getId());

        // 취소 가능한 상태들
        EnumSet<OrderStatus> cancellableStatus = EnumSet.of(OrderStatus.REQUESTED, OrderStatus.PENDING);

        if (!isAdmin && !isOrder) { throw new BusinessException(ErrorCode.INVALID_ORDER_ACCESS); }

        if (cancellableStatus.contains(order.getOrderStatus())) {
            order.changeStatusCanceled("주문 수락 전 취소한 주문입니다.");
            orderRepository.save(order);
        } else {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

    }

    // 주문 취소(점주)
    @Transactional
    public void cancelOrderOwner(User user, CancelOrderOwnerRequestDto dto) {
        Order order = orderRepository.findById(dto.orderId()).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        // 추가 검증 필요, 주문이 사장의 가게 주문인지
        if (!(user.getRole().name().equals("ADMIN") || (user.getRole().name().equals("OWNER")))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }
        order.changeStatusCanceled(dto.cancelMessage());
        orderRepository.save(order);
    }

    // 주문 상태 변경, 고객은 PENDING → REQUESTED 만 가능
    //               점주는 requested -> accepted ->  ~
    @Transactional
    public void updateOrderStatus(User user, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        OrderStatus currentStatus = order.getOrderStatus();
        String role = user.getRole().name();

        switch (role) {
            case "CUSTOMER" -> {
                if (currentStatus == OrderStatus.PENDING) {
                    order.changeStatusRequested();
                } else {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
                }
            }

            case "OWNER" -> {
                // 주문이 해당 점주의 가게에 속하는지 확인
                Restaurant findRestaurant = restaurantRepository.findById(order.getRestaurantId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));

                if (!findRestaurant.getOwnerId().equals(user.getId())) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
                }

                switch (currentStatus) {
                    case REQUESTED -> order.changeStatusAccepted();
                    case ACCEPTED -> order.changeStatusDelivering();
                    case DELIVERING -> order.changeStatusDelivered();
                    default -> throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
                }
            }

            case "ADMIN" -> {
                // 관리자는 거의 모든 상태를 강제 변경 가능
                switch (currentStatus) {
                    case PENDING -> order.changeStatusRequested();
                    case REQUESTED -> order.changeStatusAccepted();
                    case ACCEPTED -> order.changeStatusDelivering();
                    case DELIVERING -> order.changeStatusDelivered();
                    default -> throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
                }
            }
            default -> throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);

        }
        orderRepository.save(order);

    }

    // 주문 후 10분 뒤 실행될 메서드
    @Transactional
    protected void handlePendingOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getOrderStatus() == OrderStatus.PENDING) {
            order.changeStatusCanceled("결재를 진행하지 않아 취소되었습니다.");
            orderRepository.save(order);
        }
    }

}
