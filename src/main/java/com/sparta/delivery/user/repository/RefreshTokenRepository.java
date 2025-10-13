package com.sparta.delivery.user.repository;

import com.sparta.delivery.user.domain.RefreshToken;
import com.sparta.delivery.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findRefreshTokenByUser(User user);
    void deleteByUser(User user);
}
