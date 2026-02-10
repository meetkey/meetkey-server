package com.meetkey.server.domain.mission.respository;

import com.meetkey.server.domain.mission.entity.mapping.MissionLog;
import com.meetkey.server.domain.mission.entity.mapping.QMissionLog;
import com.meetkey.server.domain.mission.enums.MissionStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.meetkey.server.domain.mission.entity.mapping.QMissionLog.missionLog;
import static com.meetkey.server.domain.mission.entity.mapping.QChatRoomMission.chatRoomMission;

@RequiredArgsConstructor
public class MissionLogRepositoryImpl implements MissionLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MissionLog> findAllExpiredPendingLogs(LocalDateTime now) {
        return queryFactory
                .selectFrom(missionLog)
                .join(missionLog.chatRoomMission, chatRoomMission).fetchJoin() // missionLog와 chatRoomMission을 조인하여 fetch join 수행
                .where(
                        missionLog.missionStatus.eq(MissionStatus.PENDING), // 아직 안한 상태
                        chatRoomMission.expiresAt.before(now) // 만료된 상태

                )
                .fetch();

    }
}
