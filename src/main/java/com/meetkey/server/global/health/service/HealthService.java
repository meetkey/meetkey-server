package com.meetkey.server.global.health.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final RedisConnectionFactory redisConnectionFactory;
    private final SimpMessagingTemplate messagingTemplate;

    public Map<String, Object> check() {
        Map<String, Object> status = new HashMap<>();

        status.put("redis", checkRedis());
        status.put("websocket", checkWebSocket());

        return status;
    }

    private String checkRedis() {
        try (var connection = redisConnectionFactory.getConnection()) {
            String pong = connection.ping();
            return "PONG".equalsIgnoreCase(pong) ? "UP" : "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String checkWebSocket() {
        try {
            // Bean 존재 + 메시징 인프라 초기화 여부
            return messagingTemplate != null ? "UP" : "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }
}

