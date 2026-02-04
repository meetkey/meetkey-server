package com.meetkey.server.domain.chat.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import com.meetkey.server.global.apiPayload.exception.GeneralException;

public class ChatMessageException extends GeneralException {
    public ChatMessageException(BaseCode code){ super(code); }
}
