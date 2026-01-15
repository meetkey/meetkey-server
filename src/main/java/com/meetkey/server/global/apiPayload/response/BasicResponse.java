package com.meetkey.server.global.apiPayload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({"code", "message", "data"})
public class BasicResponse<T> {
    private final String code;
    private final String message;

    // data가 null이면 json에서 제외
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    // 성공 시 응답
    public static <T> BasicResponse<T> success(BaseCode successCode, T data){
        return BasicResponse.<T>builder()
                .code(successCode.getCode())
                .message(successCode.getMessage())
                .data(data)
                .build();
    }

    // 실패 시 응답
    public static <T> BasicResponse<T> error(BaseCode errorCode, T data){
        return BasicResponse.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(data)
                .build();
    }
}
