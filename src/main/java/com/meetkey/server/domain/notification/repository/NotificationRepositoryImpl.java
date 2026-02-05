package com.meetkey.server.domain.notification.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.meetkey.server.domain.chat.entity.QChatRoomMember.chatRoomMember;
import static com.meetkey.server.domain.member.entity.QMember.member;
import static com.meetkey.server.domain.notification.entity.QPersonalNotification.personalNotification;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public void markAsReadBySender(Member receiver, Member sender) {
        long count = queryFactory
                .update(personalNotification)
                .set(personalNotification.isRead, true) // true로 변경
                .where(
                        personalNotification.receiver.eq(receiver),
                        personalNotification.sender.eq(sender),
                        personalNotification.isRead.isFalse()

                )
                .execute();

        em.flush();
        em.clear();
    }

}
