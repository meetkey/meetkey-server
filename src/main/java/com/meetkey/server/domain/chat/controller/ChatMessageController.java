package com.meetkey.server.domain.chat.controller;

import com.meetkey.server.domain.chat.dto.pub.ChatMessagePubDTO;
import com.meetkey.server.domain.chat.dto.request.ChatMessageSendReqDTO;
import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.message.redis.ChatRedisPublisher;
import com.meetkey.server.domain.chat.service.command.ChatMessageCommandService;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "채팅 메시지 API")
@RequestMapping("/chat")
public class ChatMessageController {

    private final ChatMessageCommandService chatMessageCommandService;
    private final ChatRedisPublisher chatRedisPublisher;

    @Operation(summary = "메시지 전송 API by 슝슝", description = "채팅방에 메시지를 전송하는 API by 슝슝")
    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessageSendReqDTO req, Principal principal) {
        CustomUserDetails details = (CustomUserDetails) principal;
        Long senderId = details.getMemberId();

        ChatMessage message = chatMessageCommandService.sendMessage(
                senderId,
                req.getChatRoomId(),
                req.getMessageType(),
                req.getContent(),
                req.getMediaUrl(),
                req.getDuration() // VOICE 메시지일 때만 사용, TEXT와 IMAGE는 null
        );

        // 같은 채팅방 구독자에게 Redis로 publish
        chatRedisPublisher.publish(ChatMessagePubDTO.from(message));
    }
}
