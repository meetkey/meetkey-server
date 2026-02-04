package com.meetkey.server.domain.notification.entity;

import com.meetkey.server.domain.chat.entity.ChatMessage;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.notification.enums.NotificationType;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "personal_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @JoinColumn(name = "sender_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    @JoinColumn(name = "receiver_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    @JoinColumn(name = "chat_message_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatMessage chatMessage;

    public void renewNotification(String newContent, ChatMessage newChatMessage) {
        this.content = newContent;
        this.chatMessage = newChatMessage;
        this.isRead = false;
    }

    public void isRead(boolean isRead) {
        this.isRead = isRead;
    }

}

