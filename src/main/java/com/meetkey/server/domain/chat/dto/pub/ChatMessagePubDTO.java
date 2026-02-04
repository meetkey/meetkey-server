package com.meetkey.server.domain.chat.dto.pub;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagePubDTO {

    private Long chatRoomId;
    private Long senderId;
    private MessageType messageType;
    private String content;
    private String mediaUrl;
    private Integer duration;
    private LocalDateTime createdAt;

    public static ChatMessagePubDTO from(ChatMessage message) {
        return new ChatMessagePubDTO(
                message.getChatRoom().getId(),
                message.getMember().getId(),
                message.getMessageType(),
                message.getContent(),
                message.getMediaUrl(),
                message.getDuration(),
                message.getCreatedAt()
        );
    }
}

