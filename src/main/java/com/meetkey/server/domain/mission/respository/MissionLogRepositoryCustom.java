package com.meetkey.server.domain.mission.respository;

import com.meetkey.server.domain.mission.entity.mapping.MissionLog;

import java.time.LocalDateTime;
import java.util.List;

public interface MissionLogRepositoryCustom {
    List<MissionLog> findAllExpiredPendingLogs(LocalDateTime now);
}
