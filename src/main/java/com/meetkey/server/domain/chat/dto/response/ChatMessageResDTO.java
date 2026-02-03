package com.meetkey.server.domain.chat.dto.response;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResDTO {

    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private boolean isMine;
    private MessageType messageType;
    private String content;
    private Integer duration;
    private LocalDateTime createdAt;

    public static ChatMessageResDTO from(ChatMessage message, Long currentMemberId) {
        return new ChatMessageResDTO(
                message.getId(),
                message.getChatRoom().getId(),
                message.getMember().getId(),
                message.getMember().getId().equals(currentMemberId),
                message.getMessageType(),
                message.getContent(),
                message.getDuration(),
                message.getCreatedAt()
        );
    }
}
