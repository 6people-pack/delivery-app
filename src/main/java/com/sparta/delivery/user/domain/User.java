package com.sparta.delivery.user.domain;

import com.sparta.delivery.global.unit.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_user")
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 40, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String nickname;

    private String phoneNumber;


    @Builder
    public User(String email, String password, String nickname, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.role = Role.CUSTOMER;
    }

    // 정적 팩토리 메서드
    public static User create(String email, String password, String nickname, String phoneNumber) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = nickname;
        user.phoneNumber = phoneNumber;
        user.role = Role.CUSTOMER; // 디폴트 권한은 고객
        return user;
    }


    // 닉네임 변경 메서드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // 핸드폰번호 변경 메서드
    public void updatePhoneNumber(String phone_number) {
        this.phoneNumber = phone_number;
    }

    // 권한 변경 메서드
    public void updateRole(Role role) {
        this.role = role;
    }


}
