package com.meetkey.server.global.apiPayload.exception;

import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.code.BaseCode;
import com.meetkey.server.global.apiPayload.status.CommonErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionAdvice {
    
    // 비즈니스 로직 예외
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<BasicResponse<Void>> handleGeneralException(
            GeneralException e
    ){
        BaseCode errorCode = e.getErrorCode();

        log.warn("비즈니스 예외: code={}, message={}",
                errorCode.getCode(), errorCode.getMessage());
        
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(BasicResponse.error(errorCode, null));
    }

    // 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicResponse<String>> handleException(
            Exception e
    ){
        BaseCode errorCode = CommonErrorStatus._INTERNAL_SERVER_ERROR;
        
        log.error("예외 : type={}, message={}",
                e.getClass().getSimpleName(), e.getMessage());
        
        return ResponseEntity.status(errorCode.getStatus())
                .body(BasicResponse.error(errorCode, null));
    }

}
