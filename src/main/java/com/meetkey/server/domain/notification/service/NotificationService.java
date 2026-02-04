package com.meetkey.server.domain.notification.service;

import com.meetkey.server.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;
}
