package com.meetkey.server.domain.chat.service.command;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.enums.MessageType;
import com.meetkey.server.domain.chat.exception.ChatMessageException;
import com.meetkey.server.domain.chat.repository.ChatMessageRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomMemberRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.global.apiPayload.status.ChatErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    public ChatMessage sendMessage(Long senderId, Long chatRoomId, MessageType messageType, String content, String mediaUrl, Integer duration) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatMessageException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        boolean isParticipant = chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, senderId);

        if (!isParticipant) {throw new ChatMessageException(ChatErrorCode.NOT_CHAT_ROOM_MEMBER);}

        // 발신자 조회
        Member sender = memberRepository.findById(senderId).orElseThrow();

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .messageType(messageType)
                .content(content)
                .mediaUrl(mediaUrl)
                .duration(duration)
                .build();

        return chatMessageRepository.save(message);
    }
}
