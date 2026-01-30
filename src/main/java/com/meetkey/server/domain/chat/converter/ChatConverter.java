package com.meetkey.server.domain.chat.converter;

import com.meetkey.server.domain.chat.dto.response.ChatResDTO;
import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.ChatRoomMember;
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

    public static ChatResDTO.ChatPreviewRes toChatPreviewRes (ChatRoomMember chatRoomMember){

        Member member = chatRoomMember.getMember();
        ChatRoom chatRoom = chatRoomMember.getChatRoom();
        ChatMessage lastReadMsg = chatRoomMember.getLastReadMsg();

        return ChatResDTO.ChatPreviewRes.builder()
                .roomId(chatRoom.getId())
                .chatOpponent(ChatConverter.toChatOpponentRes(member))
                .lastChatMessages(lastReadMsg.getContent())
                // TODO: 어떤 값 넣어두기 필요해보임
//                .unReadMessageCnt(2)
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

    public static ChatResDTO.ChatMessageRes toChatMessageRes(ChatMessage chatMessage){
        return ChatResDTO.ChatMessageRes.builder()
                .messageId(chatMessage.getId())
                .senderId(chatMessage.getMember().getId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public static ChatResDTO.ChatMessageListRes toChatMessageListRes(
            ChatRoomMember chatRoomMember, List<ChatMessage> chatMessageList,
            Long nextCursor, Boolean hasNext
    ){

        List<ChatResDTO.ChatMessageRes> chatMessages = chatMessageList.stream()
                .map(ChatConverter::toChatMessageRes)
                .toList();
        ChatResDTO.ChatOpponentRes chatOpponentRes = ChatConverter.toChatOpponentRes(chatRoomMember.getMember());

        return ChatResDTO.ChatMessageListRes.builder()
                .roomId(chatRoomMember.getChatRoom().getId())
                .chatOpponent(chatOpponentRes)
                .chatMessages(chatMessages)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

}
