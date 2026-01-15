package com.meetkey.server.global.apiPayload.status;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonSuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "요청에 성공하였습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
