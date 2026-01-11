package com.meetkey.server.global.apiPayload.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseCode errorCode;
}
