package com.sparta.delivery.cartitem.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_cart_item")
@Where(clause = "deleted_at IS NULL")
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_item_id", columnDefinition = "uuid")
    private UUID id;

    private Long userId;

    private UUID restaurantId;

    private UUID menuId;

    // 옵션을 컬럼 타입을 text로 저장, json으로 받아서 처리
    @Column(columnDefinition = "TEXT")
    private String option;


    // 수량 같은 추가 정보
    private int quantity;

    // 장바구니에 메뉴 생성
    public static CartItem createCartItem(Long userId, UUID menuId, UUID restaurantId, String option, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.userId = userId;
        cartItem.menuId = menuId;
        cartItem.restaurantId = restaurantId;
        cartItem.option = option;
        cartItem.quantity = quantity;
        return cartItem;
    }

    // 수량 증가
    public void increaseQuantity() {
        this.quantity += 1;
    }

    // 수량 감소
    public void decreaseQuantity() {
        this.quantity = Math.max(1, this.quantity - 1);
    }

    // 옵션 변경
    public void updateOptions(String option) {
        this.option = option;
    }


}
