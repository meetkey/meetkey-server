package com.meetkey.server.domain.mission.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import com.meetkey.server.global.apiPayload.exception.GeneralException;

public class MissionException extends GeneralException {
    public MissionException(BaseCode code) {
        super(code);
    }
}
