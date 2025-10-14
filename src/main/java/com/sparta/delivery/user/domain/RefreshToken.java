package com.sparta.delivery.user.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_refresh_token")
public class RefreshToken extends BaseEntity {

    @Id
    @Column(name = "refresh_token_id", columnDefinition = "uuid")
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

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