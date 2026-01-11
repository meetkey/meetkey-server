package com.meetkey.server.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.meetkey.server.global.apiPayload.code.BaseCode;
import com.meetkey.server.global.apiPayload.code.ResponseDTO;
import com.meetkey.server.global.apiPayload.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "data"})
public class ApiCommonResponse <T> {
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    // 성공 시 응답
    public static <T> ApiCommonResponse<T> onSuccess(T data){
        return new ApiCommonResponse<>(
                SuccessStatus._OK.getCode(),
                SuccessStatus._OK.getMessage(),
                data
        );
    }

    // 실패 시 응답
    public static <T> ApiCommonResponse<T> onFailure(BaseCode code, T data){
        return new ApiCommonResponse<>(
                code.getCode(),
                code.getMessage(),
                data
        );
    }
}
