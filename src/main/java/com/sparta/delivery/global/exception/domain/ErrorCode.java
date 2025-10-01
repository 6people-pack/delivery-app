package com.sparta.delivery.global.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 사용 예시이므로 추가로 작성 해주시면 찡긋


    // 400
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "요청 데이터가 올바르지 않습니다. 입력 데이터를 확인해 주세요."),


    // 401
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요한 요청입니다. 로그인 해주세요.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
