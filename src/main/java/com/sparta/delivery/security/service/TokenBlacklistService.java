package com.sparta.delivery.security.service;

import com.sparta.delivery.security.domain.TokenBlacklist;
import com.sparta.delivery.security.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository blacklistRepository;

    @Transactional
    public void addToBlacklist(String token) {
        TokenBlacklist blacklistToken = TokenBlacklist.create(token);
        blacklistRepository.save(blacklistToken);
    }

    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }

}
