package com.meetkey.server.domain.chat.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResDTO {

    @Builder
    public record CreateChatRoomRes(
            Long createdChatRoomId,
            LocalDateTime createdAt
    ){}

    // 채팅방리스트
    @Builder
    public record ChatListRes(
            List<ChatResDTO.ChatPreviewRes> chatPreviewRes,
            Integer totalUnreadCnt
    ){}

    @Builder
    public record ChatPreviewRes(
            Long roomId,
            ChatOpponentRes chatOpponent,
            String lastChatMessages,
            Integer unReadMessageCnt,
            long unreadCount,
            LocalDateTime updatedAt
    ){}

    // 상세 조회용
    @Builder
    public record ChatMessageListRes(
            Long roomId,
            ChatOpponentRes chatOpponent,
            List<ChatMessageResDTO> chatMessages,
            Long nextCursor,
            Boolean hasNext
    ){}

    @Builder
    public record ChatOpponentRes(
            Long userId,
            String nickname,
            String profileImageUrl
    ){}

}
