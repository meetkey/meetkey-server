package com.meetkey.server.global.apiPayload.status;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements BaseCode {

    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM_404_1", "해당 채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_ROOM_MEMBER_404_1", "해당 채팅 멤버를 찾을 수 없습니다.")
        ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
