package com.meetkey.server.global.apiPayload.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;

public class ChatException extends GeneralException{

    public ChatException(BaseCode errorCode) {
        super(errorCode);
    }
}
