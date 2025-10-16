//package com.sparta.delivery.user.repository;
//
//import com.sparta.delivery.auth.domain.RefreshToken;
//import com.sparta.delivery.user.domain.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//
//    void deleteByUser(User user);
//    Optional<RefreshToken> findByUser(User user);
//}
