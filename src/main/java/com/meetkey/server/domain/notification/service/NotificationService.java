package com.meetkey.server.domain.notification.service;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.notification.entity.PersonalNotification;
import com.meetkey.server.domain.notification.enums.NotificationType;
import com.meetkey.server.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    // 알림 전송 및 저장
    public void sendNotification(Member sender, Member receiver, NotificationType type, String messageContent, ChatMessage chatMessage) {

        String title = makeTitle(sender, type);

        if (type == NotificationType.MESSAGE) {
            handleMessageNotification(sender, receiver, type, title, messageContent, chatMessage);
        } else {
            // 메시지 외의 알림은 무조건 새로 생성
            createNotification(sender, receiver, type, title, messageContent, chatMessage);
        }

    }

    private void handleMessageNotification(Member sender, Member receiver, NotificationType type, String title, String content, ChatMessage chatMessage) {
        // 이 사람이 보낸 '안 읽은' 메시지 알림이 이미 있는지 확인
        Optional<PersonalNotification> existingNoti = notificationRepository
                .findTopByReceiverAndSenderAndTypeAndIsReadFalse(receiver, sender, type);

        if (existingNoti.isPresent()) {
            // 안 읽은 게 있다 -> 기존 알림 내용만 갱신 (Update 쿼리 나감)
            existingNoti.get().renewNotification(content, chatMessage);
            log.info("기존 알림 갱신 완료: notificationId={}", existingNoti.get().getId());
        } else {
            // 다 읽었거나 없음 -> 새로 생성 (Insert 쿼리 나감)
            createNotification(sender, receiver, type, title, content, chatMessage);
        }
    }

    private void createNotification(Member sender, Member receiver, NotificationType type, String title, String content, ChatMessage chatMessage) {
        PersonalNotification notification = PersonalNotification.builder()
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .title(title)
                .content(content)
                .isRead(false) // 기본값 안 읽음
                .chatMessage(chatMessage) // 채팅 메시지 연결 (null일 수도 있음)
                .build();

        notificationRepository.save(notification);
        log.info("새 알림 저장 완료: receiverId={}, type={}", receiver.getId(), type);
    }


    private String makeTitle(Member sender, NotificationType type) {
        return switch (type) {
            case MESSAGE -> sender.getName(); // 메시지는 보낸 사람 이름이 제목
            case MATCH -> "✨ 새로운 매칭 성공!";
            case INCOMING_CALL -> "📞 전화 요청이 왔습니다.";
            case MISSED_CALL -> "❌ 부재중 전화 알림";
            default -> "알림";
        };
    }



}
