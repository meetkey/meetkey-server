package com.meetkey.server.domain.chat.dto.response;

import com.meetkey.server.domain.chat.dto.pub.ChatMessagePubDTO;
import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
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

    public static ChatMessageResDTO fromPub(ChatMessagePubDTO pub, Long receiverId) {
        log.info("senderId={}, receiverId={}, equals={}",
                pub.getSenderId(),
                receiverId,
                pub.getSenderId().equals(receiverId));

        return ChatMessageResDTO.builder()
                .messageId(pub.getMessageId())
                .chatRoomId(pub.getChatRoomId())
                .senderId(pub.getSenderId())
                .isMine(pub.getSenderId().equals(receiverId))
                .messageType(pub.getMessageType())
                .content(pub.getContent())
                .duration(pub.getDuration())
                .createdAt(pub.getCreatedAt())
                .build();
    }

}
