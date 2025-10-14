package com.sparta.delivery.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Up 부분 대문자 아닌데 파일명 고치기가 안됨..

public record SignUpRequestDto(

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @NotBlank(message = "이메일 입력은 필수 입력값입니다.")
    String email,

    @NotBlank(message = "비밀번호 입력은 필수 입력값입니다.")
    String password,

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    String nickname,

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    String phoneNumber

) {}

/**TODO 요구사항
 - [ ]  username은  `최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)`로 구성
- [ ]  password는  `최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자`
- [ ]  사용자 권한 (`CUSTOMER`, `OWNER`, `MANAGER`, `MASTER`)도 필요합니다.
(MASTER는 최종관리자, MANAGER는 서비스 담당자들 입니다.) **/