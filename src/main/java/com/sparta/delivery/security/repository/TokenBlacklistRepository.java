package com.sparta.delivery.security.repository;

import com.sparta.delivery.security.domain.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
    boolean existsByToken(String token);
}
