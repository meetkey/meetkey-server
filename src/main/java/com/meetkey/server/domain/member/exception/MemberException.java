package com.meetkey.server.domain.member.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import com.meetkey.server.global.apiPayload.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(BaseCode code){ super(code); }
}