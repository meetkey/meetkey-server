package com.meetkey.server.domain.member.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorStatus implements BaseCode {
    /**
     * 1xx: 클라이언트가 수정해야 할 입력값 문제
     * 2xx: 서버에서 리소스를 찾을 수 없는 문제
     * 3xx: 권한/인증 문제
     * 4xx: 비즈니스 로직 위반
     */

    // 회원 관련
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4041", "해당 사용자를 찾을 수 없습니다."),
    INVALID_S3_KEY(HttpStatus.NOT_FOUND, "MEMBER4042", "저장된 프로필 사진을 찾을 수 없습니다."),
    ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "MEMBER_4001", "이미 차단된 사용자입니다."),
    BLOCKED_MEMBER(HttpStatus.FORBIDDEN, "MEMBER4031", "차단당한 or 차단한 사용자의 프로필은 조회 불가능 합니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}