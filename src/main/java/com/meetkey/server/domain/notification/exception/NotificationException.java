package com.meetkey.server.domain.notification.exception;

import com.meetkey.server.global.apiPayload.exception.GeneralException;

public class NotificationException extends GeneralException {
    public NotificationException(NotificationErrorStatus errorStatus) {
        super(errorStatus);
    }
}
