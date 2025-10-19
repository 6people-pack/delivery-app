package com.sparta.delivery.user.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import com.sparta.delivery.user.dto.*;
import com.sparta.delivery.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/signup")
    public BaseResponse<Void> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        userService.signup(signUpRequestDto);
        return BaseResponse.ok(BaseStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public BaseResponse<Void> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        userService.login(loginRequestDto, response);
        return BaseResponse.ok(BaseStatus.OK); //200
    }

    // jwt 토큰 재발급
    @PostMapping("/token-refresh")
    public BaseResponse<Void> refreshJwtToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest request,
            HttpServletResponse response) {

        userService.refreshJwtToken(userDetails.getUser(), request, response);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 로그아웃
    @PostMapping("/logout")
    public BaseResponse<Void> logout(
            HttpServletRequest request) {

        userService.logout(request);
        return BaseResponse.ok(BaseStatus.OK); //200
    }

    // 내 정보 변경
    @PatchMapping("/update")
    public BaseResponse<Void> update(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateRequestDto dto) {

        userService.update(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 회원 탈퇴
    @PatchMapping("/withdrawal")
    public BaseResponse<Void> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.withdraw(userDetails.getUser());
        return BaseResponse.ok(BaseStatus.OK);
    }

}
