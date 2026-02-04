package com.meetkey.server.domain.chat.service.command;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.chat.entity.ChatRoomMember;
import com.meetkey.server.domain.chat.entity.enums.MessageType;
import com.meetkey.server.domain.chat.exception.ChatErrorStatus;
import com.meetkey.server.domain.chat.exception.ChatMessageException;
import com.meetkey.server.domain.chat.repository.ChatMessageRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomMemberRepository;
import com.meetkey.server.domain.chat.repository.ChatRoomRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.notification.enums.NotificationType;
import com.meetkey.server.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    private final NotificationService notificationService;

    public ChatMessage sendMessage(Long senderId, Long chatRoomId, MessageType messageType, String content, String mediaUrl, Integer duration) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatMessageException(ChatErrorStatus.CHAT_ROOM_NOT_FOUND));

        boolean isParticipant = chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, senderId);

        if (!isParticipant) throw new ChatMessageException(ChatErrorStatus.NOT_CHAT_ROOM_MEMBER);

        // 발신자 조회
        Member sender = memberRepository.findById(senderId).orElseThrow();

        // duration이 필요한 메시지 타입이라면 설정
        Integer resolvedDuration = null;
        if (messageType.requiresDuration()) {
            if (duration == null) throw new ChatMessageException(ChatErrorStatus.DURATION_REQUIRED);
            resolvedDuration = duration;
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .messageType(messageType)
                .content(content)
                .mediaUrl(mediaUrl)
                .duration(resolvedDuration)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        List<ChatRoomMember> members = chatRoomMemberRepository.findAllByChatRoomId(chatRoomId);
        for (ChatRoomMember target : members) {
//            if (target.getMember().getId().equals(senderId)) continue; // 본인 제외

            // 알림을 끈 사람은 제외
            if (!target.isAlarm()) continue;

            String notificationContent = getNotificationContent(messageType, content);

            notificationService.sendNotification(
                    sender,
                    target.getMember(),
                    NotificationType.MESSAGE,
                    notificationContent,
                    savedMessage
            );
        }

        return savedMessage;
    }

    private String getNotificationContent(MessageType type, String content) {
        return switch (type) {
            case TEXT -> content;
            case IMAGE -> "사진을 보냈습니다.";
            case VOICE -> "음성 메시지를 보냈습니다.";
        };
    }
}
