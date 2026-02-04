package com.meetkey.server.global.websocket.mapper;

import com.meetkey.server.global.apiPayload.status.CommonErrorStatus;
import com.meetkey.server.global.websocket.code.StompErrorCode;

public final class StompErrorMapper {

    private StompErrorMapper() {}

    public static CommonErrorStatus mapToHttp(StompErrorCode stompCode) {

        return switch (stompCode) {

            case STOMP_AUTH_HEADER_MISSING,
                 STOMP_INVALID_TOKEN,
                 STOMP_TOKEN_EXPIRED ->
                    CommonErrorStatus._UNAUTHORIZED;

            case STOMP_UNAUTHORIZED, CHAT_ROOM_FORBIDDEN ->
                    CommonErrorStatus._FORBIDDEN;

            case CHAT_ROOM_NOT_FOUND ->
                    CommonErrorStatus._NOT_FOUND;

            case STOMP_INTERNAL_ERROR ->
                    CommonErrorStatus._INTERNAL_SERVER_ERROR;
        };
    }
}
