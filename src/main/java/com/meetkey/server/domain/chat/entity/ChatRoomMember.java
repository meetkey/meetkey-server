package com.meetkey.server.domain.chat.entity;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "chat_room_member",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"chat_room_id", "member_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChatRoomMember extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "chat_room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_msg_id")
    private ChatMessage lastReadMsg;

    private LocalDateTime joinedAt;

    @Column(nullable = false)
    private boolean isAlarm = true;

    public void updateLastReadMsg(ChatMessage lastReadMsg){
        if (this.lastReadMsg != null &&
                this.lastReadMsg.getId() >= lastReadMsg.getId()) {
            return;
        }

        this.lastReadMsg = lastReadMsg;
    }

    public void toggleAlarm(boolean isAlarm) {
        this.isAlarm = isAlarm;
    }
}
