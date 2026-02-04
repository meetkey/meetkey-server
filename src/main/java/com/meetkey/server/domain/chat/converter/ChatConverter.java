package com.meetkey.server.domain.chat.converter;

import com.meetkey.server.domain.chat.dto.response.ChatMessageResDTO;
import com.meetkey.server.domain.chat.dto.response.ChatResDTO;
import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.ChatRoomMember;
import com.meetkey.server.domain.chat.entity.enums.ChatRoomType;
import com.meetkey.server.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public class ChatConverter {

    public static ChatResDTO.CreateChatRoomRes createChatRoomRes(Long chatRoomId, LocalDateTime createdAt){
        return ChatResDTO.CreateChatRoomRes.builder()
                .createdChatRoomId(chatRoomId)
                .createdAt(createdAt)
                .build();
    }

    public static ChatRoom toChatRoom(String directKey){
        return ChatRoom.builder()
                .directKey(directKey)
                .type(ChatRoomType.DIRECT)
                .build();
    }

    public static ChatRoomMember toChatRoomMember(Member member, ChatRoom chatRoom, ChatMessage chatMessage){
        return ChatRoomMember.builder()
                .member(member)
                .chatRoom(chatRoom)
                .lastReadMsg(chatMessage)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public static ChatResDTO.ChatPreviewRes toChatPreviewRes (ChatRoomMember chatRoomMember, ChatMessage lastMessage, long unreadCount){

        Member member = chatRoomMember.getMember();
        ChatRoom chatRoom = chatRoomMember.getChatRoom();
        ChatMessage lastReadMsg = chatRoomMember.getLastReadMsg();

        return ChatResDTO.ChatPreviewRes.builder()
                .roomId(chatRoom.getId())
                .chatOpponent(ChatConverter.toChatOpponentRes(member))
                .lastChatMessages(lastMessage != null ? lastMessage.getContent() : null)
                .unreadCount(unreadCount)
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
    }

    public static ChatResDTO.ChatOpponentRes toChatOpponentRes(Member member){
        return ChatResDTO.ChatOpponentRes.builder()
                .userId(member.getId())
                .nickname(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

    public static ChatResDTO.ChatMessageListRes toChatMessageListRes(
            ChatRoomMember chatRoomMember,
            List<ChatMessage> chatMessageList,
            Long nextCursor,
            Boolean hasNext,
            Long currentMemberId
    ){
        List<ChatMessageResDTO> chatMessages = chatMessageList.stream()
                .map(msg -> ChatMessageResDTO.from(msg, currentMemberId))
                .toList();

        ChatResDTO.ChatOpponentRes chatOpponentRes =
                ChatConverter.toChatOpponentRes(chatRoomMember.getMember());

        return ChatResDTO.ChatMessageListRes.builder()
                .roomId(chatRoomMember.getChatRoom().getId())
                .chatOpponent(chatOpponentRes)
                .chatMessages(chatMessages)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }


}
