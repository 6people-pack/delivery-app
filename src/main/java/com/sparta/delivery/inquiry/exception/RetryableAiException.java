package com.sparta.delivery.inquiry.exception;

public class RetryableAiException extends RuntimeException {
    public RetryableAiException(String msg) { super(msg); }
    public RetryableAiException(String msg, Throwable cause) { super(msg, cause); }
}