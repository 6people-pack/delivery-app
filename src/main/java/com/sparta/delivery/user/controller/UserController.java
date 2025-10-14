package com.sparta.delivery.user.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import com.sparta.delivery.user.dto.*;
import com.sparta.delivery.user.service.UserService;
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

    @GetMapping("/test") //스프링 시큐리티 인증 테스트
    public String loginTest(){
        return "login test";
    }

    @PostMapping("/signup")
    public BaseResponse<Void> signup(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        userService.signup(signUpRequestDto);
        return BaseResponse.ok(BaseStatus.CREATED);
    }

    @PostMapping("/login")
    public BaseResponse<Void> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        userService.login(loginRequestDto, response);
        return BaseResponse.ok(BaseStatus.OK); //200
    }

    // 핸드폰 번호 변경
    @PatchMapping("/me/updatePhoneNumber")
    public BaseResponse<Void> updatePhoneNumber(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdatePhoneNumberDto dto) {

        userService.updatePhoneNumber(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 닉네임 변경
    @PatchMapping("/me/updateNickname")
    public BaseResponse<Void> updateNickname(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdateNicknameDto dto) {

        userService.updateNickname(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 현재 비밀번호 확인
    @PatchMapping("/me/verifyPassword")
    public BaseResponse<Void> verifyPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody VerifyPasswordDto dto) {

        userService.verifyPassword(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 비밀번호 변경
    @PatchMapping("/me/updatePassword")
    public BaseResponse<Void> updatePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdatePasswordDto dto) {

        userService.updatePassword(userDetails.getUser(), dto);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 회원 탈퇴
    @PatchMapping("/me/withdrawal")
    public BaseResponse<Void> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.withdraw(userDetails.getUser());
        return BaseResponse.ok(BaseStatus.OK);
    }

}
