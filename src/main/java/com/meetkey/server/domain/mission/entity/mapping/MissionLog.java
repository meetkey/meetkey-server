package com.meetkey.server.domain.mission.entity.mapping;


import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.mission.enums.MissionStatus;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Table(name = "mission_log")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MissionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MissionStatus missionStatus;

    private LocalDateTime completedAt;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Member member;

    @JoinColumn(name = "chat_room_mission_id")
    @ManyToOne(fetch = FetchType.LAZY)
    ChatRoomMission chatRoomMission;

    public void complete() {
        this.missionStatus = MissionStatus.SUCCESS;
        this.completedAt = LocalDateTime.now();
    }

    public void fail() {
        this.missionStatus = MissionStatus.FAILED;
    }
}
