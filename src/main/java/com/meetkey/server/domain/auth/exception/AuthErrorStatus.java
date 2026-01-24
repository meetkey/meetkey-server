package com.meetkey.server.domain.auth.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorStatus implements BaseCode {
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4001", "토큰 형식이 잘못되었습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4011", "토큰이 만료되었습니다."),
    VERIFY_FAILED(HttpStatus.BAD_REQUEST, "AUTH4002", "인증번호가 일치하지 않습니다."),
    SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5001", "인증번호 발송에 실패했습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}

