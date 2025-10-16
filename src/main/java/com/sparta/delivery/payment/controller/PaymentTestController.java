package com.sparta.delivery.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentTestController {

    // 토스페이 테스트용 로그인 페이지
    @GetMapping("/api/user/login")
    public String getLoginPage() {
        return "redirect:/login.html";
    }

}
