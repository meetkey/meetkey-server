package com.meetkey.server.global.apiPayload.exception;

import com.meetkey.server.global.apiPayload.ApiCommonResponse;
import com.meetkey.server.global.apiPayload.code.BaseCode;
import com.meetkey.server.global.apiPayload.status.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionAdvice {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiCommonResponse<Void>> handleGeneralException(
            GeneralException e
    ){
        BaseCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiCommonResponse.onFailure(
                        errorCode,
                        null
                    )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiCommonResponse<String>> handleException(
            Exception e
    ){
        BaseCode errorCode = ErrorStatus._INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiCommonResponse.onFailure(
                        errorCode,
                        e.getMessage()
                    )
                );
    }

}
