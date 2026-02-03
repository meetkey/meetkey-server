package com.meetkey.server.global.websocket.security;

import com.meetkey.server.global.websocket.code.StompErrorCode;
import com.meetkey.server.global.websocket.exception.StompException;
import com.meetkey.server.global.security.CustomUserDetails;
import com.meetkey.server.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // CONNECT 프레임에서만 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new StompException(StompErrorCode.STOMP_AUTH_HEADER_MISSING, "Authorization header is missing or invalid");
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.isValid(token, true)) {
                throw new StompException(StompErrorCode.STOMP_INVALID_TOKEN, "JWT is expired or invalid");
            }

            String memberId = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            CustomUserDetails user = new CustomUserDetails(memberId, role);
            accessor.setUser(user);
        }

        return message;
    }
}
