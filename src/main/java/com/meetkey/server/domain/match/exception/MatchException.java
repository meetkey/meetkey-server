package com.meetkey.server.domain.match.exception;

import com.meetkey.server.global.apiPayload.exception.GeneralException;

public class MatchException extends GeneralException {
    public MatchException(MatchErrorStatus errorStatus) {
        super(errorStatus);
    }
}
