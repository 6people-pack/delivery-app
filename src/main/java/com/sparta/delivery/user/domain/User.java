package com.sparta.delivery.user.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_user")
@Where(clause = "deleted_at IS NULL")
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 40, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(name = "refresh_token")
    private String refreshToken;

    // 고객 회원 생성
    public static User createCustomer(String email, String password, String nickname, String phoneNumber) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = nickname;
        user.phoneNumber = phoneNumber;
        user.role = Role.CUSTOMER; // 역할 초기화
        return user;
    }

    // 리프레시 토큰 변경 메서드
    public void updateRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    // 닉네임 변경 메서드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // 핸드폰번호 변경 메서드
    public void updatePhoneNumber(String phone_number) {
        this.phoneNumber = phone_number;
    }

    // 비밀번호 변경 메서드
    public void updatePassword(String password) {
        this.password = password;
    }

    // 권한 변경 메서드
    public void updateRole(Role role) {
        this.role = role;
    }


}
