package com.meetkey.server.domain.match.service;

import com.meetkey.server.domain.badge.enums.BadgeLevel;
import com.meetkey.server.domain.chat.repository.ChatRoomMemberRepository;
import com.meetkey.server.domain.match.dto.*;
import com.meetkey.server.domain.match.entity.RecommendationQueue;
import com.meetkey.server.domain.match.enums.MatchType;
import com.meetkey.server.domain.match.exception.MatchErrorStatus;
import com.meetkey.server.domain.match.exception.MatchException;
import com.meetkey.server.domain.match.repository.MatchRepository;
import com.meetkey.server.domain.match.repository.RecommendationQueueRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.Preference;
import com.meetkey.server.domain.member.entity.mapping.FromToId;
import com.meetkey.server.domain.member.entity.mapping.MemberLike;
import com.meetkey.server.domain.member.entity.mapping.MemberLocation;
import com.meetkey.server.domain.member.enums.Membership;
import com.meetkey.server.domain.member.repository.MemberLikeRepository;
import com.meetkey.server.domain.member.repository.MemberLocationRepository;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.member.repository.PreferenceRepository;
import com.meetkey.server.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final MemberLocationRepository memberLocationRepository;
    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final RecommendationQueueRepository recommendationQueueRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final com.meetkey.server.domain.badge.respository.PointHistoryRepository pointHistoryRepository;
    private final S3Service s3Service;

    @Transactional
    @Override
    public MatchListResDTO getRecommendations(Long memberId, RecommendationReqDTO request) {
        Member member = getMember(memberId);

        // 0. 기존 큐 초기화 (필터 변경 시 반영을 위해)
        recommendationQueueRepository.deleteByMemberAndIsSwipedFalse(member);

        // 1. 금일 스와이프 횟수 확인 (일일 제한 10명)
        int remainingQuota;
        if (member.getMembership() == Membership.PREMIUM) {
            remainingQuota = 10; // 유료 회원은 제한 없음 (항상 10명 풀 요청 가능)
        } else {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
            long todaySwipes = memberLikeRepository.countByFromMemberAndCreatedAtBetween(member, startOfDay, endOfDay);
            remainingQuota = Math.max(0, 10 - (int) todaySwipes);
        }

        // 2. 분기 처리: 일일 할당량 남음 vs 소진 (Recycle)
        boolean forceRecycle = false;
        if (remainingQuota > 0) {
            // A. Daily Match Mode (새로울 후보 생성)
            // 할당량(remainingQuota)만큼만 생성
            List<RecommendationResDTO> newRecommendations = generateRecommendations(member, request, remainingQuota);

            if (!newRecommendations.isEmpty()) {
                return buildResponse(newRecommendations, newRecommendations.size(), newRecommendations.size(), MatchType.DAILY_MATCH);
            }

            // 추천된 후보가 없을 때, 프리미엄 유저는 Recycle 모드로 전환
            if (member.getMembership() == Membership.PREMIUM) {
                forceRecycle = true;
            } else {
                return buildResponse(Collections.emptyList(), 0, 0, MatchType.DAILY_MATCH);
            }
        }

        if (remainingQuota <= 0 || forceRecycle) {
            // B. Recycle Mode (할당량 소진 시 또는 프리미엄 유저 후보 없음)
            // 재활용 대상: DISLIKE 했거나, LIKE 했지만 채팅 시작 안 한 유저
            List<Member> recycleMembers = memberLikeRepository.findRecycleMembers(member);

            // 채팅방이 존재하는 유저 제외
            List<Long> chattedMemberIds = chatRoomMemberRepository.findChattedMemberIds(member);
            recycleMembers = recycleMembers.stream()
                .filter(m -> !chattedMemberIds.contains(m.getId()))
                .collect(Collectors.toList());

            // 랜덤으로 섞어서 반환 (또는 최신순 등)
            Collections.shuffle(recycleMembers);
            List<Member> limitedRecycle = recycleMembers.stream().limit(10).toList();

            List<MemberLocation> recycleLocations = memberLocationRepository.findAllByMemberIn(limitedRecycle);
            Map<Long, MemberLocation> recycleLocMap = recycleLocations.stream()
                .collect(Collectors.toMap(loc -> loc.getMember().getId(), loc -> loc));

            MemberLocation myLocation = memberLocationRepository.findByMember(member).orElse(null);

            List<RecommendationResDTO> dtos = limitedRecycle.stream()
                .map(target -> {
                    Preference pref = preferenceRepository.findById(target.getId()).orElse(null);
                    MemberLocation targetLoc = recycleLocMap.get(target.getId());
                    double distance = 0.0;
                    if (myLocation != null && targetLoc != null) {
                        distance = calculateDistance(myLocation.getLatitude(), myLocation.getLongitude(),
                            targetLoc.getLatitude(), targetLoc.getLongitude());
                    }
                    return convertToDTO(target, pref, distance);
                })
                .collect(Collectors.toList());

            MatchType type = MatchType.RECYCLE;
            int remaining = 0;
            if (member.getMembership() == Membership.PREMIUM) {
                type = MatchType.DAILY_MATCH;
                remaining = 10;
            }

            return buildResponse(dtos, remaining, dtos.size(), type);
        }

        return buildResponse(Collections.emptyList(), 0, 0, MatchType.DAILY_MATCH);
    }

    private MatchListResDTO buildResponse(List<RecommendationResDTO> list, int remaining, int total, MatchType type) {
        return MatchListResDTO.builder()
            .recommendations(list)
            .swipeInfo(MatchListResDTO.SwipeInfoDTO.builder()
                .remainingCount(remaining)
                .totalCount(total)
                .build())
            .matchType(type)
            .build();
    }

    private List<RecommendationResDTO> generateRecommendations(Member member, RecommendationReqDTO request, int limit) {
        // 1. 기 스와이프 유저 제외
        List<Long> excludedIds = memberLikeRepository.findSwipedMemberIdsByMember(member);
        // 1.1 이미 채팅한 유저 제외
        excludedIds.addAll(chatRoomMemberRepository.findChattedMemberIds(member));

        // 1.5. 위치 정보 보정 (요청에 좌표 없고 DB에 있으면 DB 값 사용)
        MemberLocation myLocation = memberLocationRepository.findByMember(member).orElse(null);
        if (request.maxDistance() != null) {
            if ((request.latitude() == null || request.longitude() == null) && myLocation == null) {
                throw new MatchException(MatchErrorStatus.LOCATION_NOT_FOUND);
            }

            if (request.latitude() == null || request.longitude() == null) {
                request = RecommendationReqDTO.builder()
                    .interests(request.interests())
                    .homeTown(request.homeTown())
                    .nativeLanguage(request.nativeLanguage())
                    .targetLanguage(request.targetLanguage())
                    .targetLanguageLevel(request.targetLanguageLevel())
                    .minAge(request.minAge())
                    .maxAge(request.maxAge())
                    .latitude(myLocation.getLatitude())
                    .longitude(myLocation.getLongitude())
                    .maxDistance(request.maxDistance())
                    .build();
            }
        }

        // 2. 하드 필터 (QueryDSL)
        List<Member> candidates = matchRepository.findRecommendableMembers(member, request, excludedIds, 100);

        // 3. 소프트 스코어링 (점수 계산)
        Preference myPreference = preferenceRepository.findById(member.getId()).orElse(null);

        // 후보자들의 Preference 일괄 조회
        List<Long> candidateIds = candidates.stream().map(Member::getId).collect(Collectors.toList());
        List<Preference> preferences = preferenceRepository.findAllById(candidateIds);
        Map<Long, Preference> prefMap = preferences.stream()
            .collect(Collectors.toMap(p -> p.getMember().getId(), p -> p));

        // 후보자들의 Location 일괄 조회
        List<MemberLocation> locations = memberLocationRepository.findAllByMemberIn(candidates);
        Map<Long, MemberLocation> locMap = locations.stream()
            .collect(Collectors.toMap(loc -> loc.getMember().getId(), loc -> loc));

        List<MemberScore> scoredCandidates = candidates.stream()
            .map(candidate -> {
                Preference targetPref = prefMap.get(candidate.getId());
                MemberLocation targetLoc = locMap.get(candidate.getId());
                double score = calculateScore(member, myLocation, myPreference, candidate, targetPref, targetLoc);
                return new MemberScore(candidate, score);
            })
            .sorted((p1, p2) -> Double.compare(p2.score, p1.score))
            .limit(limit)
            .collect(Collectors.toList());

        // 10명 미만일 경우 랜덤 유저 백필 (엄격한 조건 적용)
        if (scoredCandidates.size() < limit) {
            int needed = limit - scoredCandidates.size();
            List<Long> currentIds = scoredCandidates.stream().map(ms -> ms.member().getId()).toList();
            List<Long> allExcluded = new ArrayList<>(excludedIds);
            allExcluded.addAll(currentIds);

            List<Member> randomMembers = matchRepository.findRandomMembers(member, request, allExcluded, needed); // 랜덤 멤버는 Preference 점수 0 처리 (또는 조회 로직 추가 필요)

            randomMembers.forEach(rm -> {
                // 랜덤 멤버의 Preference 조회 (개별 조회 허용 or 추가 벌크 조회) - 여기선 개별 조회
                // Preference randomPref = preferenceRepository.findById(rm.getId()).orElse(null);
                // scoredCandidates.add(new MemberScore(rm, 0.0)); // 점수는 0
                // DTO 변환 시 필요하므로, 일단 점수 매기는 로직에 넣지 말고 0으로 하고, DTO 변환 시 조회하도록 함.
                scoredCandidates.add(new MemberScore(rm, 0.0));

                // Location map에 없으면 추가 조회 필요할 수 있음
                if (!locMap.containsKey(rm.getId())) {
                    memberLocationRepository.findByMember(rm).ifPresent(loc -> locMap.put(rm.getId(), loc));
                }
            });
        }

        // 4. 큐에 저장
        List<RecommendationQueue> queueEntities = scoredCandidates.stream()
            .map(ms -> RecommendationQueue.builder()
                .member(member)
                .targetMember(ms.member())
                .score(ms.score())
                .isSwiped(false)
                .isRecycled(false)
                .build())
            .collect(Collectors.toList());

        recommendationQueueRepository.saveAll(queueEntities);

        return scoredCandidates.stream()
            .map(ms -> {
                // 백필된 멤버의 경우 prefMap에 없을 수 있음
                Preference targetPref = prefMap.containsKey(ms.member().getId()) ?
                    prefMap.get(ms.member().getId()) :
                    preferenceRepository.findById(ms.member().getId()).orElse(null);

                MemberLocation targetLoc = locMap.containsKey(ms.member().getId()) ?
                    locMap.get(ms.member().getId()) :
                    memberLocationRepository.findByMember(ms.member()).orElse(null);

                double distance = 0.0;
                if (myLocation != null && targetLoc != null) {
                    distance = calculateDistance(myLocation.getLatitude(), myLocation.getLongitude(),
                        targetLoc.getLatitude(), targetLoc.getLongitude());
                }

                return convertToDTO(ms.member(), targetPref, distance);
            })
            .collect(Collectors.toList());
    }

    private double calculateScore(Member me, MemberLocation myLoc, Preference myPref, Member target, Preference targetPref, MemberLocation targetLoc) {
        double score = 0;

        // 1. 언어 점수 (최대 +5점)
        boolean meTargetMatch = me.getTargetLanguage() == target.getFirstLanguage();
        boolean targetTargetMatch = target.getTargetLanguage() == me.getFirstLanguage();

        if (meTargetMatch && targetTargetMatch) score += 5;
        else if (meTargetMatch || targetTargetMatch) score += 3;
        else if (me.getTargetLanguage() == target.getTargetLanguage()) score += 1;

        // 2. 성격 점수 (최대 +5점)
        if (myPref != null && targetPref != null) {
            if (myPref.getSocialType() == targetPref.getSocialType()) score += 1;
            if (myPref.getMeetingType() == targetPref.getMeetingType()) score += 1;
            if (myPref.getChatType() == targetPref.getChatType()) score += 1;
            if (myPref.getFriendType() == targetPref.getFriendType()) score += 1;
            if (myPref.getRelationType() == targetPref.getRelationType()) score += 1;
        }

        // 3. 관심사 점수 (+N점)

        // 3. 관심사 점수 (+N점)
        List<String> myInterests = me.getInterestMembers().stream()
            .map(im -> im.getInterest().getType().name())
            .toList();
        List<String> targetInterests = target.getInterestMembers().stream()
            .map(im -> im.getInterest().getType().name())
            .toList();
        score += myInterests.stream().filter(targetInterests::contains).count();

        // 4. 보너스 점수 (+2점)
        // 연령대 일치 (+1점)
        if (me.getBirthday() != null && target.getBirthday() != null) {
            int myAgeGroup = (LocalDate.now().getYear() - me.getBirthday().getYear()) / 10;
            int targetAgeGroup = (LocalDate.now().getYear() - target.getBirthday().getYear()) / 10;
            if (myAgeGroup == targetAgeGroup) score += 1;
        }

        // 거리 50km 이내 (+1점)
        if (myLoc != null && targetLoc != null) {
            double dist = calculateDistance(myLoc.getLatitude(), myLoc.getLongitude(), targetLoc.getLatitude(), targetLoc.getLongitude());
            if (dist <= 50.0) score += 1;
        }

        return score;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = R * c;
        return Math.round(dist * 100) / 100.0;
    }

    private RecommendationResDTO convertToDTO(Member member) {
        // Recycle 모드 등 단일 조회 시 사용
        Preference pref = preferenceRepository.findById(member.getId()).orElse(null);
        return convertToDTO(member, pref, 0.0);
    }

    private RecommendationResDTO convertToDTO(Member member, Preference pref, double distance) {
        int age = member.getBirthday() != null ? LocalDate.now().getYear() - member.getBirthday().getYear() + 1 : 0;

        int totalScore = pointHistoryRepository.calculateTotalScore(member);
        String badgeLevel = BadgeLevel.fromScore(totalScore).name();

        String location = member.getLocation();

        RecommendationResDTO.PersonalityDTO personalityDTO = null;
        if (pref != null) {
            personalityDTO = RecommendationResDTO.PersonalityDTO.builder()
                .socialType(pref.getSocialType())
                .meetingType(pref.getMeetingType())
                .chatType(pref.getChatType())
                .friendType(pref.getFriendType())
                .relationType(pref.getRelationType())
                .build();
        }

        // 관심사 목록 조회
        List<String> interests = member.getInterestMembers().stream()
            .map(im -> im.getInterest().getType().name())
            .collect(Collectors.toList());

        return RecommendationResDTO.builder()
            .targetMemberId(member.getId())
            .nickname(member.getName())
            .age(age)
            .hometown(member.getHomeTown())
            .distance(distance)
            .gender(member.getGender())
            .nativeLanguage(RecommendationResDTO.LanguageDTO.builder()
                .language(member.getFirstLanguage())
                .level(null) // 본인 언어 레벨은 보통 null
                .build())
            .targetLanguage(RecommendationResDTO.LanguageDTO.builder()
                .language(member.getTargetLanguage())
                .level(member.getTargetLanguageLevel())
                .build())
            .interests(interests)
            .personality(personalityDTO)
            .photoUrls(Collections.singletonList(s3Service.generateGetPresignedUrl(member.getProfileImageUrl())))// 플레이스홀더
            .introduction(member.getBio())
            .badge(RecommendationResDTO.BadgeInfoDTO.builder()
                .level(badgeLevel)
                .score(totalScore)
                .build())
            .location(location)
            .build();
    }

    @Override
    @Transactional
    public SwipeResDTO swipe(Long memberId, SwipeReqDTO request) {
        Member member = getMember(memberId);

        if (member.getId().equals(request.targetMemberId())) {
            throw new MatchException(MatchErrorStatus.SELF_SWIPE_NOT_ALLOWED);
        }

        Member target = memberRepository.findById(request.targetMemberId())
            .orElseThrow(() -> new MatchException(MatchErrorStatus.TARGET_MEMBER_NOT_FOUND));

        // 좋아요/싫어요 저장
        MemberLike memberLike = MemberLike.builder()
            .memberLikeId(new FromToId(member.getId(), target.getId()))
            .fromMember(member)
            .toMember(target)
            .action(request.action())
            .isMatched(false) // 매칭(상호 좋아요) 확인 로직 추가 가능
            .build();
        memberLikeRepository.save(memberLike);

        // 큐에서 스와이프 처리
        recommendationQueueRepository.findByMemberAndTargetMember(member, target)
            .ifPresent(RecommendationQueue::markSwiped);

        return SwipeResDTO.builder()
            .targetMemberId(target.getId())
            .action(request.action())
            .build();
    }

    private record MemberScore(Member member, double score) {
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new MatchException(MatchErrorStatus.MEMBER_NOT_FOUND));
    }
}
