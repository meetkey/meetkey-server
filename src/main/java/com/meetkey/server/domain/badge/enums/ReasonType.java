package com.meetkey.server.domain.badge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReasonType {
    AUTH("본인 인증", 20),
    PROFILE("상세 프로필 작성 완료", 15),
    ACTIVE("활동적인 멤버", 25),
    POSITIVE("긍정적인 평가", 15),
    MISSION_SUCCESS("미션 완료", 3),
    MISSION_FAILURE("미션 실패", -5);


    private final String description;
    private final int defaultScore;
}
