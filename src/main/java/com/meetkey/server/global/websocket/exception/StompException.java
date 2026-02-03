package com.meetkey.server.global.websocket.exception;

import com.meetkey.server.global.websocket.code.StompErrorCode;
import lombok.Getter;

@Getter
public class StompException extends RuntimeException {

    private final StompErrorCode errorCode;

    public StompException(StompErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
