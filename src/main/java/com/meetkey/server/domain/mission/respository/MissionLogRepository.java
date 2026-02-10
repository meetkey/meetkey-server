package com.meetkey.server.domain.mission.respository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.mission.entity.mapping.ChatRoomMission;
import com.meetkey.server.domain.mission.entity.mapping.MissionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionLogRepository extends JpaRepository<MissionLog, Long>, MissionLogRepositoryCustom {
    Optional<MissionLog> findByChatRoomMissionAndMember(ChatRoomMission chatRoomMission, Member member);
}
