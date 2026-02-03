package com.meetkey.server.domain.chat.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import com.meetkey.server.global.apiPayload.exception.GeneralException;

public class ChatException extends GeneralException {

    public ChatException(BaseCode errorCode) {
        super(errorCode);
    }
}
