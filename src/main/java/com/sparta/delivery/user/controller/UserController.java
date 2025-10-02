package com.sparta.delivery.user.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.user.dto.LoginRequestDto;
import com.sparta.delivery.user.dto.SignUpRequestDto;
import com.sparta.delivery.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public BaseResponse<Void> login(@RequestBody @Valid LoginRequestDto loginRequest, HttpServletResponse response) {
        userService.login(loginRequest, response);
        return BaseResponse.ok(BaseStatus.OK); //200
    }
}
