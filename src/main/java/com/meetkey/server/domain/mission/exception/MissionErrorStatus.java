package com.meetkey.server.domain.mission.exception;

import com.meetkey.server.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MissionErrorStatus implements BaseCode {

    ALREADY_CLEAR(HttpStatus.BAD_REQUEST, "MISSION4001", "이미 완료된 미션입니다."),
    NOT_COMPLETED_YET(HttpStatus.BAD_REQUEST, "MISSION4002", "아직 미션을 수행하지 않았습니다."),
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION4041", "미션을 찾을 수 없습니다."),
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION4042", "참여 정보를 찾을 수 없습니다.")
    ;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
