package com.sparta.delivery.security.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_token_blacklist")
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_blacklist_id", columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 500)
    private String token;

    // 만료 시간 기록 후 일정 시간마다 만료 시간이 지난 토큰 삭제
    @Column(nullable = false)
    private LocalDateTime exp;

    public static TokenBlacklist create(String token, LocalDateTime exp) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.token = token;
        tokenBlacklist.exp = exp;
        return tokenBlacklist;
    }

}