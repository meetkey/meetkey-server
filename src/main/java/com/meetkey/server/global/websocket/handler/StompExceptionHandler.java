package com.meetkey.server.global.websocket.handler;

import com.meetkey.server.global.websocket.exception.StompException;
import com.meetkey.server.global.websocket.mapper.StompErrorMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if (ex instanceof StompException stompEx) {
            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

            // STOMP 에러 코드
            accessor.setMessage(stompEx.getErrorCode().name());

            // HTTP 의미 매핑
            accessor.addNativeHeader(
                    "mapped-http-status",
                    StompErrorMapper
                            .mapToHttp(stompEx.getErrorCode())
                            .getStatus()
                            .toString()
            );

            accessor.setContentType(MimeTypeUtils.TEXT_PLAIN);

            byte[] payload = stompEx.getMessage() != null ? stompEx.getMessage().getBytes(StandardCharsets.UTF_8) : new byte[0];

            return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }
}