package com.meetkey.server.domain.mission.dto;

import com.meetkey.server.domain.badge.enums.BadgeLevel;
import com.meetkey.server.domain.mission.enums.MissionStatus;
import com.meetkey.server.domain.mission.enums.MissionType;
import lombok.Builder;

public class MissionResDTO {

    // 오늘의 미션 조회 응답
    @Builder
    public record Info(
            Long missionId,
            String content,
            MissionType type,
            long remainingSeconds, // 남은 시간 (초)
            MissionStatus myStatus // PENDING, SUCCESS, FAILED
    ) {}

    // 미션 완료 응답
    @Builder
    public record Completion(
            String status,         // "SUCCESS"
            int gainedPoints,      // 획득 점수 (+3)
            int currentScore,      // 현재 총 점수
            String badgeName
    ) {}
}
