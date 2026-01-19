package com.meetkey.server.domain.chat.entity;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chat_room_id", nullable = false)
    @ManyToOne
    private ChatRoom chatRoom;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne
    private Member member;

    @Column(nullable = false)
    private String type; // 메시지 유형 -> enum 으로 변경

    @Column(length = 20, nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String mediaUrl;

    private int duration;
}
