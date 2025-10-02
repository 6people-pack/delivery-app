package com.sparta.delivery.user.mapper;

import com.sparta.delivery.user.domain.User;
import com.sparta.delivery.user.dto.SignUpRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapper {
    public static User toUser(SignUpRequestDto dto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))  // 암호화
                .nickname(dto.nickname())
                .phoneNumber(dto.phoneNumber())
                .build();

    }
}
