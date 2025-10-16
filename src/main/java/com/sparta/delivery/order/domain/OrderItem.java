package com.sparta.delivery.order.domain;

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
@Table(name = "p_order_item")
@Where(clause = "deleted_at IS NULL")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", columnDefinition = "uuid")
    private UUID id;


    private String menuName;

    private int menuPrice;

    private int menuDiscountPrice;

    private int quantity;

    private String option;

    private UUID menuId;

    public static OrderItem create(String menuName, int menuPrice, int menuDiscountPrice, int quantity, String option, UUID menuId) {
        OrderItem item = new OrderItem();
        item.menuName = menuName;
        item.menuPrice = menuPrice;
        item.menuDiscountPrice = menuDiscountPrice;
        item.quantity = quantity;
        item.option = option;
        item.menuId = menuId;
        return item;
    }

}
