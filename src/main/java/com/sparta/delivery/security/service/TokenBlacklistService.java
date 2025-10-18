package com.sparta.delivery.security.service;

import com.sparta.delivery.security.domain.TokenBlacklist;
import com.sparta.delivery.security.jwt.utils.JwtUtil;
import com.sparta.delivery.security.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository blacklistRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public void addToBlacklist(String token) {

        Claims info = jwtUtil.getUserInfoFromToken(token);
        Date exp = info.getExpiration();

        TokenBlacklist blacklistToken = TokenBlacklist.create(token, exp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        blacklistRepository.save(blacklistToken);
    }

    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }

    // 하루에 한번 만료 시간이 지난 블랙리스트 토큰 삭제
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    protected void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        blacklistRepository.deleteExpiredTokens(now);
        System.out.println("[Scheduler] 만료된 블랙리스트 토큰 삭제 완료: " + now);
    }

}
