package com.meetkey.server.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.FcmToken;
import com.meetkey.server.domain.notification.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    @Async
    public void sendPush(Member receiver, String title, String body) {

        // 받는 사람의 토큰 조회
        List<FcmToken> tokens = fcmTokenRepository.findAllByMember(receiver);

        if (tokens.isEmpty()) {
            log.info("FCM 토큰이 없어 알림을 건너뜁니다. memberId: {}", receiver.getId());
            return;
        }

        // 토큰 별로 전송
        for (FcmToken fcmToken : tokens) {
            try {
                Notification notification = Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

                Message message = Message.builder()
                        .setToken(fcmToken.getToken())
                        .setNotification(notification)
                        .build();

                FirebaseMessaging.getInstance().send(message);
                log.info("FCM 전송 성공: memberId={}, token={}", receiver.getId(), fcmToken.getToken());
            } catch (Exception e) {
                log.error("FCM 전송 실패: token={}", fcmToken.getToken(), e);
            }
        }
    }
}
