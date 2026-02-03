package com.meetkey.server.domain.chat.dto.response;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResDTO {

    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private MessageType messageType;
    private String content;
    private String mediaUrl;
    private Integer duration;
    private LocalDateTime createdAt;

    public static ChatMessageResDTO from(ChatMessage message) {
        return new ChatMessageResDTO(
                message.getId(),
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
