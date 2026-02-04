package com.meetkey.server.domain.chat.message.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetkey.server.domain.chat.dto.pub.ChatMessagePubDTO;
import com.meetkey.server.domain.chat.message.ChatMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final ChatMessagePublisher chatMessagePublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessagePubDTO dto = objectMapper.readValue(
                    message.getBody(),
                    ChatMessagePubDTO.class
            );
            chatMessagePublisher.publish(dto);

        } catch (Exception e) {
            log.error("Redis chat message consume error", e);
        }
    }
}
