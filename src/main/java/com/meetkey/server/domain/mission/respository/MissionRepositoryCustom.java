package com.meetkey.server.domain.mission.respository;

import com.meetkey.server.domain.mission.entity.Mission;

import java.util.Optional;

public interface MissionRepositoryCustom {
    Optional<Mission> findRandomMission();
}
