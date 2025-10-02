package com.sparta.delivery.user.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    //단방향으로 유저와 연결
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date exp;

    @Builder
    public RefreshToken(String refreshToken, User user, Date exp) {
        this.refreshToken = refreshToken;
        this.user = user;
        this.exp = exp;
    }

}