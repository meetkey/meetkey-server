package com.meetkey.server.domain.notification.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.notification.entity.PersonalNotification;
import com.meetkey.server.domain.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<PersonalNotification, Long>, NotificationRepositoryCustom {

    // 특정 송신자 -> 수신자에게 특정 타입의 읽지 않은 알림이 있는지 조회
    Optional<PersonalNotification> findTopByReceiverAndSenderAndTypeAndIsReadFalse(
            Member receiver,
            Member sender,
            NotificationType type
    );




}
