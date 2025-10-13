package com.sparta.delivery.cartitem.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import com.sparta.delivery.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_cart_item")
public class CartItem extends BaseEntity {

    @Id
    @Column(name = "cart_item_id", columnDefinition = "UUID")
    private UUID id;

    @PrePersist                 // persist()되기 전(= DB에 저장되기 전) 호출
    public void generateId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    // 사용자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 메뉴와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private TempMenu menu;

    // 옵션을 컬럼 타입을 text로 저장, json으로 받아서 처리
    @Column(columnDefinition = "TEXT")
    private String options;


    // 수량 같은 추가 정보
    private int quantity;

    public CartItem(User user, TempMenu menu, String options, int quantity) {
        this.user = user;
        this.menu = menu;
        this.options = options;
        this.quantity = quantity;
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
    public void updateOptions(String options) {
        this.options = options;
    }


}
