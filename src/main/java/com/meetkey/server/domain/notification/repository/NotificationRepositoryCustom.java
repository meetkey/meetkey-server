package com.meetkey.server.domain.notification.repository;

import com.meetkey.server.domain.member.entity.Member;

import java.util.Optional;

public interface NotificationRepositoryCustom {
    void markAsReadBySender(Member receiver, Member sender);

}
