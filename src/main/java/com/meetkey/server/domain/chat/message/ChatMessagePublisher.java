package com.meetkey.server.domain.chat.message;

import com.meetkey.server.domain.chat.dto.response.ChatMessageResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishToUser(Long receiverId, ChatMessageResDTO dto) {
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),   // memberId
                "/queue/chat",            // destination
                dto
        );
    }
}
