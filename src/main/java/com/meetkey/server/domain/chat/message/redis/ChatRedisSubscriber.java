package com.meetkey.server.domain.chat.message.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetkey.server.domain.chat.dto.pub.ChatMessagePubDTO;
import com.meetkey.server.domain.chat.dto.response.ChatMessageResDTO;
import com.meetkey.server.domain.chat.message.ChatMessagePublisher;
import com.meetkey.server.domain.chat.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final ChatMessagePublisher chatMessagePublisher;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessagePubDTO dto = objectMapper.readValue(message.getBody(), ChatMessagePubDTO.class);
            List<Long> memberIds = chatRoomMemberRepository.findMemberIdsByChatRoomId(dto.getChatRoomId());

            for (Long receiverId : memberIds) {
                ChatMessageResDTO res = ChatMessageResDTO.fromPub(dto, receiverId);
                chatMessagePublisher.publishToUser(receiverId, res);
            }
        } catch (Exception e) {
            log.error("Redis chat message consume error", e);
        }
    }

}
