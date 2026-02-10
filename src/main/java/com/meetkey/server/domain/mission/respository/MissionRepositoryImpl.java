package com.meetkey.server.domain.mission.respository;

import com.meetkey.server.domain.mission.entity.Mission;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.meetkey.server.domain.mission.entity.QMission.mission;


@RequiredArgsConstructor
public class MissionRepositoryImpl implements MissionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Mission> findRandomMission() {
        NumberTemplate<Double> randomOrder = Expressions.numberTemplate(Double.class, "function('rand')");

        Mission randomMission = queryFactory
                .selectFrom(mission)
                .orderBy(randomOrder.asc()) // 랜덤 정렬
                .limit(1) // 하나만
                .fetchFirst();

        return Optional.ofNullable(randomMission);
    }
}
