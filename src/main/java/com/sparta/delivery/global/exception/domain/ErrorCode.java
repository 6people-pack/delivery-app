package com.sparta.delivery.global.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 사용 예시이므로 추가로 작성 해주시면 찡긋


    //// 400
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "요청 데이터가 올바르지 않습니다. 입력 데이터를 확인해 주세요."),

    //tosspay
    ZERO_AMOUNT_PAYMENT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "0원 결제는 허용되지 않습니다."),



    // 401
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요한 요청입니다. 로그인 해주세요."),

    // JWT) 관련
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),


    // 403 Forbidden
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),


    // 404 Not Found
    LOGIN_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "로그인 정보와 일치하는 사용자가 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),
    NOT_FIND_INQUIRY(HttpStatus.NOT_FOUND, "문의 정보가 존재하지 않습니다."),

    // 500 Server Error
    AI_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "AI 응답 서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.");



    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
