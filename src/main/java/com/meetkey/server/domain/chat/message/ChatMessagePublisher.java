package com.meetkey.server.domain.chat.message;

import com.meetkey.server.domain.chat.dto.pub.ChatMessagePubDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publish(ChatMessagePubDTO dto) {
        messagingTemplate.convertAndSend(
                "/sub/chat/" + dto.getChatRoomId(),
                dto
        );
    }
}
