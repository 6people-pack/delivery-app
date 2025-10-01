package com.sparta.delivery.global.exception.handler;

import com.sparta.delivery.global.exception.BusinessException;
import com.sparta.delivery.global.exception.domain.ErrorCode;
import com.sparta.delivery.global.exception.dto.ErrorResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode code = e.getErrorCode();

        String path = request.getMethod() + " " + request.getRequestURI();

        return ResponseEntity.status(code.getStatus()).body(ErrorResponse.builder()
                .status(code.getStatus().value())
                .code(code.name())
                .message(code.getMessage())
                .path(path)
                .build());
    }
    // path랑 code.name 빠질 거 같음


}