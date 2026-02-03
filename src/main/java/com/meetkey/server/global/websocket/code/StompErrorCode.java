package com.meetkey.server.global.websocket.code;

public enum StompErrorCode {

    STOMP_AUTH_HEADER_MISSING,
    STOMP_INVALID_TOKEN,
    STOMP_TOKEN_EXPIRED,
    STOMP_UNAUTHORIZED,

    CHAT_ROOM_NOT_FOUND,
    CHAT_ROOM_FORBIDDEN,

    STOMP_INTERNAL_ERROR
}
