package com.meetkey.server.domain.mission.respository;

import com.meetkey.server.domain.chat.entity.ChatRoom;
import com.meetkey.server.domain.mission.entity.Mission;
import com.meetkey.server.domain.mission.entity.mapping.ChatRoomMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomMissionRepository extends JpaRepository<ChatRoomMission, Long> {

    Optional<ChatRoomMission> findFirstByChatRoomOrderByAssignedAtDesc(ChatRoom chatRoom);

    Long mission(Mission mission);
}
