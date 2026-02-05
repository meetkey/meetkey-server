package com.meetkey.server.domain.notification.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorStatus implements BaseCode {
    // 400 Bad Request
    NOTIFICATION_FORBIDDEN(HttpStatus.FORBIDDEN, "NOTIFICATION4031", "해당 알림에 대한 권한이 없습니다.."),

    // 404 Not Found
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION4041", "존재하지 않는 알림입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
