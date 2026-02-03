package com.meetkey.server.domain.chat.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorStatus implements BaseCode {
    /**
     * 1xx: 클라이언트가 수정해야 할 입력값 문제
     * 2xx: 서버에서 리소스를 찾을 수 없는 문제
     * 3xx: 권한/인증 문제
     * 4xx: 비즈니스 로직 위반
     */

    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT2041", "존재하지 않는 채팅방입니다."),
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT2042", "해당 메시지를 찾을 수 없습니다."),
    CHAT_ROOM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM_MEMBER_404_1", "해당 채팅 멤버를 찾을 수 없습니다."),
    NOT_CHAT_ROOM_MEMBER(HttpStatus.FORBIDDEN, "CHAT3031", "해당 채팅방의 참여자가 아닙니다."),
    DURATION_REQUIRED(HttpStatus.BAD_REQUEST, "INPUT1001", "duration이 누락되었습니다. 음성 메시지의 길이를 입력해주세요."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
