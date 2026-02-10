package com.meetkey.server.domain.mission.respository;

import com.meetkey.server.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long>, MissionRepositoryCustom {

}
