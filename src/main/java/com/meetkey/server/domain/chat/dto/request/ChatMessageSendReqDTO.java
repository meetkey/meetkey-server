package com.meetkey.server.domain.chat.dto.request;

import com.meetkey.server.domain.chat.entity.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageSendReqDTO {
    private Long chatRoomId;
    private MessageType messageType;
    private String content;
    private String mediaUrl;
    private Integer duration;
}
