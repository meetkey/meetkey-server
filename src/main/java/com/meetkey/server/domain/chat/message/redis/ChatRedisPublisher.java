package com.meetkey.server.domain.chat.message.redis;

import com.meetkey.server.domain.chat.dto.pub.ChatMessagePubDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChatMessagePubDTO dto) {
        String channel = "chat.room." + dto.getChatRoomId();
        redisTemplate.convertAndSend(channel, dto);
    }
}
