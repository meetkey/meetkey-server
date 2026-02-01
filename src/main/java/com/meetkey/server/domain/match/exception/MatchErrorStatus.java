package com.meetkey.server.domain.match.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MatchErrorStatus implements BaseCode {
    // 400 Bad Request
    SELF_SWIPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "MATCH4001", "본인을 스와이프할 수 없습니다."),
    ALREADY_SWIPED(HttpStatus.BAD_REQUEST, "MATCH4002", "이미 스와이프한 상대입니다."),

    // 404 Not Found
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH4041", "위치 정보를 찾을 수 없습니다."),
    TARGET_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH4042", "대상을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH4043", "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
