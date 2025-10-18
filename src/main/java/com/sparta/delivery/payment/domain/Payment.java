package com.sparta.delivery.payment.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import com.sparta.delivery.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private Long amount;

    private String paymentKey;


    @Column(nullable = false, length = 1000)
    private String payStatusMessage;

    // 되는지 아직 모름
    private String method; // 결제 수단
    private String provider; // 결제 제공사


    @Builder
    public Payment(Long userId, String userName, UUID orderId, Long amount, String paymentKey, String payStatusMessage, String method, String provider) {
        this.userId = userId;
        this.userName = userName;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.payStatusMessage = payStatusMessage;
    }
}
