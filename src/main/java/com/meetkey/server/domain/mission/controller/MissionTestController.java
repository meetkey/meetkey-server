package com.meetkey.server.domain.mission.controller;

import com.meetkey.server.domain.mission.service.MissionService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test - Mission", description = "[테스트용] 미션 스케줄러 강제 실행 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/missions")
public class MissionTestController {

    private final MissionService missionService;

    @Operation(summary = "만료된 미션 강제 실패 처리",
            description = "스케줄러를 기다리지 않고, 현재 시간 기준으로 만료된 미션을 즉시 실패 처리(-5점) 합니다.")
    @PostMapping("/expire-now")
    public BasicResponse<String> triggerExpire() {
        int count = missionService.processExpiredMission();
        return BasicResponse.success(CommonSuccessStatus._OK, "수동 실행 완료. 총 " + count + "건의 미션이 실패 처리되었습니다.");
    }
}
