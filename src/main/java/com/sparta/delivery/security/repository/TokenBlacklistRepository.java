package com.sparta.delivery.security.repository;

import com.sparta.delivery.security.domain.TokenBlacklist;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.exp < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
