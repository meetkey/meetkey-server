package com.meetkey.server.domain.chat.service.command;

import com.meetkey.server.domain.chat.converter.ChatConverter;
import com.meetkey.server.domain.chat.dto.request.ChatReqDTO;
import com.meetkey.server.domain.chat.dto.response.ChatResDTO;
import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.ChatRoomMember;
import com.meetkey.server.domain.chat.exception.ChatErrorStatus;
import com.meetkey.server.domain.chat.repository.ChatMessageRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomMemberRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.chat.exception.ChatException;
import com.meetkey.server.domain.notification.service.NotificationService;
import com.meetkey.server.global.apiPayload.exception.GeneralException;
import com.meetkey.server.global.apiPayload.status.CommonErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    // 채팅방 생성
    public ChatResDTO.CreateChatRoomRes createChatRoom(ChatReqDTO.CreateChatRoomReq req, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
        Member targetMember = memberRepository.findById(req.targetUserId())
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        String directKey = makeDirectKey(memberId, req.targetUserId());
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByDirectKey(directKey);

        // 채팅방 존재할 경우
        if (optionalChatRoom.isPresent()) {
            ChatRoom chatRoom = optionalChatRoom.get();
            return ChatConverter.createChatRoomRes(chatRoom.getId(), chatRoom.getCreatedAt());
        }

        // 존재하지 않을 경우 생성
        ChatRoom savedChatRoom = chatRoomRepository.save(ChatConverter.toChatRoom(directKey));
        ChatRoomMember chatRoomMember = ChatConverter.toChatRoomMember(member, savedChatRoom, null);
        ChatRoomMember chatRoomTargetMember = ChatConverter.toChatRoomMember(targetMember, savedChatRoom, null);
        chatRoomMemberRepository.save(chatRoomMember);
        chatRoomMemberRepository.save(chatRoomTargetMember);

        return ChatConverter.createChatRoomRes(savedChatRoom.getId(), savedChatRoom.getCreatedAt());
    }

    // 채팅방 나가기
    public void deleteChatRoom(Long memberId, Long chatRoomId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._INTERNAL_SERVER_ERROR));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));
        ChatRoomMember byMemberAndChatRoom = chatRoomMemberRepository.findByMemberAndChatRoom(member, chatRoom)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND));
        chatRoomMemberRepository.delete(byMemberAndChatRoom);
    }

    // 메세지 읽음 처리
    public void readMessages(Long memberId, Long chatRoomId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(CommonErrorStatus._INTERNAL_SERVER_ERROR));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));
        ChatRoomMember myChatRoomMember =
                chatRoomMemberRepository.findByMemberAndChatRoom(member, chatRoom)
                        .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND));

        // 알림 로직 -> 상대방이 보낸 알림들 읽음 처리
        Member partner = chatRoomMemberRepository.findPartner(chatRoomId, memberId)
                .orElse(null);
        if (partner != null) {
            notificationService.readNotificationBySender(member, partner);
        }

        // 채팅방의 가장 최신 메시지 조회
        ChatMessage latestMessage = chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(chatRoom).orElse(null);
        if (latestMessage == null) return;

        // 이미 최신까지 읽은 상태라면 업데이트 안함
        ChatMessage lastReadMsg = myChatRoomMember.getLastReadMsg();
        if (lastReadMsg != null && lastReadMsg.getId() >= latestMessage.getId()) return;

        myChatRoomMember.updateLastReadMsg(latestMessage);
    }


    private String makeDirectKey(Long a, Long b) {
        return (a < b) ? a + ":" + b : b + ":" + a;
    }

    // 채팅방 알림 설정/해제 (토글)
    public void toggleAlarm(Long memberId, Long chatRoomId, boolean isAlarm) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));

        ChatRoomMember myChatRoomMember =
                chatRoomMemberRepository.findByMemberAndChatRoom(member, chatRoom)
                        .orElseThrow(() -> new ChatException(ChatErrorStatus.CHAT_ROOM_MEMBER_NOT_FOUND));

        myChatRoomMember.toggleAlarm(isAlarm);
    }

}
