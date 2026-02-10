package com.meetkey.server.domain.mission.entity.mapping;

import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.mission.entity.Mission;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Table(name = "chat_room_mission")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime assignedAt;
    private LocalDateTime expiresAt;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @JoinColumn(name = "mission_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Mission mission;

}
