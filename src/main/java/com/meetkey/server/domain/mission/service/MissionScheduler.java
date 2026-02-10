package com.meetkey.server.domain.mission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MissionScheduler {

    private final MissionService missionService;

    // 1시간 마다 실행 -> (매시 정각 0분 0초)
    @Scheduled(cron = "0 0 * * * *")
    public void checkExpiredMissions() {
        log.info("[Scheduler] 미션 만료 체크 스케줄러 실행");
        try {
            int processedCount = missionService.processExpiredMission();
            log.info("[Scheduler] 처리 완료: {}건", processedCount);
        } catch (Exception e) {
            log.error("[Scheduler] 미션 만료 처리 중 에러 발생", e);
        }
    }
}
