package com.meetkey.server.domain.auth.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorStatus implements BaseCode {
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH4001", "토큰 형식이 잘못되었습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4011", "토큰이 만료되었습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}

