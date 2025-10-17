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

    @Column(length = 40, nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 고객 회원 생성
    public static User createCustomer(String email, String password, String name, String nickname, String phoneNumber, Role role) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.name = name;
        user.nickname = nickname;
        user.phoneNumber = phoneNumber;
        user.role = (role != null) ? role : Role.CUSTOMER;
        return user;
    }

    // 정보 수정 메서드
    public void update(String password, String name, String nickname, String phoneNumber){
        if (password != null) this.password = password;
        if (name != null) this.name = name;
        if (nickname != null) this.nickname = nickname;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
    }

    // 리프레시 토큰 변경 메서드
    public void updateRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    // 권한 변경 메서드
    public void updateRole(Role role) {
        this.role = role;
    }


}
