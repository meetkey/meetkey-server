package com.meetkey.server.domain.mission.converter;

import com.meetkey.server.domain.badge.entity.Badge;
import com.meetkey.server.domain.badge.enums.ReasonType;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.mission.entity.mapping.ChatRoomMission;
import com.meetkey.server.domain.mission.entity.mapping.MissionLog;
import org.springframework.stereotype.Component;

import static com.meetkey.server.domain.mission.dto.MissionResDTO.*;

@Component
public class MissionConverter {

    // 오늘의 미션 조회 응답 변환
    public Info toMissionInfo(ChatRoomMission chatRoomMission, MissionLog missionLog, long remainingSeconds) {
        return Info.builder()
                .missionId(chatRoomMission.getId())
                .content(chatRoomMission.getMission().getContent())
                .type(chatRoomMission.getMission().getMissionType())
                .remainingSeconds(remainingSeconds)
                .myStatus(missionLog.getMissionStatus())
                .build();
    }

    // 미션 완료 응답 변환
    public Completion toMissionCompletion(Badge badge, ReasonType reasonType) {
        return Completion.builder()
                .status("SUCCESS")
                .gainedPoints(reasonType.getDefaultScore())
                .currentScore(badge.getTotal_score())
                .badgeName(badge.getLevel().name())
                .build();
    }


}
