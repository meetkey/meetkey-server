package com.meetkey.server.domain.match.repository;

import com.meetkey.server.domain.match.dto.RecommendationReqDTO;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.QInterest;
import com.meetkey.server.domain.member.entity.QMember;
import com.meetkey.server.domain.member.entity.QPreference;
import com.meetkey.server.domain.member.entity.mapping.QInterestMember;
import com.meetkey.server.domain.member.entity.mapping.QMemberLocation;
import com.meetkey.server.domain.member.enums.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MatchRepositoryImpl implements MatchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findRecommendableMembers(Member member, RecommendationReqDTO request, List<Long> excludedIds, int limit) {
        QMember qMember = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        // 1. 기본 필터 (상태, 본인 제외, 기 스와이프 유저 제외)
        builder.and(qMember.status.eq(Status.ACTIVE));
        builder.and(qMember.id.ne(member.getId()));
        builder.and(qMember.id.notIn(excludedIds));


        // 2. DTO 기반 동적 필터링
        if (request.homeTown() != null && !request.homeTown().isEmpty())
            builder.and(qMember.homeTown.in(request.homeTown()));
        if (request.nativeLanguage() != null && !request.nativeLanguage().isEmpty())
            builder.and(qMember.firstLanguage.in(request.nativeLanguage()));
        if (request.targetLanguage() != null && !request.targetLanguage().isEmpty())
            builder.and(qMember.targetLanguage.in(request.targetLanguage()));
        if (request.targetLanguageLevel() != null && !request.targetLanguageLevel().isEmpty())
            builder.and(qMember.targetLanguageLevel.in(request.targetLanguageLevel()));

        // 나이 필터 (한국 만 나이 계산: 현재 연도 - 생년 + 1)
        if (request.minAge() != null) {
            builder.and(qMember.birthday.year().loe(LocalDate.now().getYear() + 1 - request.minAge()));
        }
        if (request.maxAge() != null) {
            builder.and(qMember.birthday.year().goe(LocalDate.now().getYear() + 1 - request.maxAge()));
        }

        var query = queryFactory.selectFrom(qMember);

        // 관심사 필터 (Join 필요)
        if (request.interests() != null && !request.interests().isEmpty()) {
            QInterestMember qInterestMember = QInterestMember.interestMember;
            QInterest qInterest = QInterest.interest;

            query.leftJoin(qMember.interestMembers, qInterestMember)
                .leftJoin(qInterestMember.interest, qInterest);
            builder.and(qInterest.type.in(request.interests()));
            query.distinct();
        }

        // 거리 필터 (Join, Haversine 공식)
        if (request.maxDistance() != null && request.latitude() != null && request.longitude() != null) {
            QMemberLocation qMemberLocation = QMemberLocation.memberLocation;
            query.leftJoin(qMemberLocation).on(qMemberLocation.member.eq(qMember));

            // Haversine 공식 (km 단위)
            NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                request.latitude(), qMemberLocation.latitude, qMemberLocation.longitude, request.longitude());

            builder.and(qMemberLocation.isNotNull()); // 위치 정보가 있는 경우만
            builder.and(distanceExpression.loe(request.maxDistance()));
        }

        // 3. 성향 필터 (Preference Join)
        // 3. 성향 필터 (Preference Join + Single List Parsing)
        if (request.personalities() != null && !request.personalities().isEmpty()) {
            QPreference qPreference = QPreference.preference;
            query.leftJoin(qPreference).on(qPreference.member.eq(qMember));

            List<String> personalities = request.personalities();

            // Enum 별 분류 리스트
            List<SocialType> socialTypes = new ArrayList<>();
            List<MeetingType> meetingTypes = new ArrayList<>();
            List<ChatType> chatTypes = new ArrayList<>();
            List<FriendType> friendTypes = new ArrayList<>();
            List<RelationType> relationTypes = new ArrayList<>();

            for (String p : personalities) {
                // Ignore "ANY" or duplicates in logic if handled by isValidEnum
                try {
                    socialTypes.add(SocialType.valueOf(p));
                    continue;
                } catch (IllegalArgumentException e) {
                }

                try {
                    // MeetingType.ANY 무시 or 로직에 따라 포함 (여기선 무시)
                    MeetingType mt = MeetingType.valueOf(p);
                    if (mt != MeetingType.ANY) meetingTypes.add(mt);
                    continue;
                } catch (IllegalArgumentException e) {
                }

                try {
                    chatTypes.add(ChatType.valueOf(p));
                    continue;
                } catch (IllegalArgumentException e) {
                }

                try {
                    // FriendType.ANY 무시
                    FriendType ft = FriendType.valueOf(p);
                    if (ft != FriendType.ANY) friendTypes.add(ft);
                    continue;
                } catch (IllegalArgumentException e) {
                }

                try {
                    relationTypes.add(RelationType.valueOf(p));
                    continue;
                } catch (IllegalArgumentException e) {
                }
            }

            if (!socialTypes.isEmpty()) builder.and(qPreference.socialType.in(socialTypes));
            if (!meetingTypes.isEmpty()) builder.and(qPreference.meetingType.in(meetingTypes));
            if (!chatTypes.isEmpty()) builder.and(qPreference.chatType.in(chatTypes));
            if (!friendTypes.isEmpty()) builder.and(qPreference.friendType.in(friendTypes));
            if (!relationTypes.isEmpty()) builder.and(qPreference.relationType.in(relationTypes));
        }

        return query.where(builder)
            .limit(limit)
            .fetch();
    }

    @Override
    public List<Member> findRandomMembers(Member member, RecommendationReqDTO request, List<Long> excludedIds, int limit) {
        QMember qMember = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        // 1. 기본 필터
        builder.and(qMember.status.eq(Status.ACTIVE));
        builder.and(qMember.id.ne(member.getId()));
        builder.and(qMember.id.notIn(excludedIds));


        // 2. 엄격한 백필 필터 (나이, 거리만 적용)
        // 나이 필터
        if (request.minAge() != null) {
            builder.and(qMember.birthday.year().loe(java.time.LocalDate.now().getYear() + 1 - request.minAge()));
        }
        if (request.maxAge() != null) {
            builder.and(qMember.birthday.year().goe(java.time.LocalDate.now().getYear() + 1 - request.maxAge()));
        }

        var query = queryFactory.selectFrom(qMember);

        // 거리 필터
        if (request.maxDistance() != null && request.latitude() != null && request.longitude() != null) {
            QMemberLocation qMemberLocation = QMemberLocation.memberLocation;
            query.leftJoin(qMemberLocation).on(qMemberLocation.member.eq(qMember));

            NumberExpression<Double> distanceExpression = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1})))",
                request.latitude(), qMemberLocation.latitude, qMemberLocation.longitude, request.longitude());

            builder.and(qMemberLocation.isNotNull());
            builder.and(distanceExpression.loe(request.maxDistance()));
        }

        return query.where(builder)
            .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
            .limit(limit)
            .fetch();
    }
}
