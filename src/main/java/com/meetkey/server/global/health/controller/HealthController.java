package com.meetkey.server.global.health.controller;

import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.health.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "헬스체크 API")
@RequestMapping("/health")
public class HealthController {

    private final HealthService healthService;

    @Operation(summary = "서버 헬스 체크", description = "Redis 및 WebSocket 서버 상태를 확인합니다.")
    @GetMapping
    public BasicResponse<Map<String, Object>> health() {
        return BasicResponse.success(CommonSuccessStatus._OK, healthService.check());
    }

}
