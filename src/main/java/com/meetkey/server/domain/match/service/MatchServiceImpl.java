package com.meetkey.server.domain.match.service;

import com.meetkey.server.domain.match.dto.*;
import com.meetkey.server.domain.match.enums.MatchType;
import com.meetkey.server.domain.match.repository.MatchRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.FromToId;
import com.meetkey.server.domain.member.entity.mapping.MemberLike;
import com.meetkey.server.domain.member.entity.mapping.MemberLocation;
import com.meetkey.server.domain.member.repository.MemberLikeRepository;
import com.meetkey.server.domain.member.repository.MemberLocationRepository;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.match.entity.RecommendationQueue;
import com.meetkey.server.domain.match.repository.RecommendationQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final MemberLocationRepository memberLocationRepository;
    private final MemberRepository memberRepository;
    private final RecommendationQueueRepository recommendationQueueRepository;

    @Override
    @Transactional
    public MatchListResDTO getRecommendations(Member member, RecommendationReqDTO request) {
        // 1. 추천 큐 확인
        List<RecommendationQueue> queue = recommendationQueueRepository.findAllByMemberAndIsSwipedFalse(member, Sort.by(Sort.Direction.DESC, "score"));

        if (!queue.isEmpty()) {
            boolean isRecycled = queue.get(0).getIsRecycled();
            MatchType currentType = isRecycled ? MatchType.RECYCLE : MatchType.DAILY_MATCH;
            List<RecommendationResDTO> dtos = queue.stream()
                .map(q -> convertToDTO(q.getTargetMember()))
                .collect(Collectors.toList());
            return buildResponse(dtos, queue.size(), currentType);
        }

        // 2. 재활용 시도 (이미 스와이프한 유저가 있다면)
        List<RecommendationQueue> recycleCandidates = recommendationQueueRepository.findTop10ByMemberAndIsSwipedTrueAndIsRecycledFalseOrderByUpdateAtDesc(member);

        // 이미 매칭된 유저는 제외
        List<RecommendationQueue> validRecycle = new ArrayList<>();
        for (RecommendationQueue item : recycleCandidates) {
            Member target = item.getTargetMember();
            // MemberLike 상태 확인
            MemberLike interaction = memberLikeRepository.findById(new FromToId(member.getId(), target.getId())).orElse(null);

            // 이미 매칭된 경우 스킵 (이미 성공한 관계)
            if (interaction != null && interaction.getIsMatched() != null && interaction.getIsMatched()) {
                continue;
            }
            validRecycle.add(item);
        }

        if (!validRecycle.isEmpty()) {
            // 큐 아이템 상태를 재활용으로 업데이트
            validRecycle.forEach(RecommendationQueue::recycle);
            recommendationQueueRepository.saveAll(validRecycle);

            List<RecommendationResDTO> dtos = validRecycle.stream()
                .map(q -> convertToDTO(q.getTargetMember()))
                .collect(Collectors.toList());
            return buildResponse(dtos, validRecycle.size(), MatchType.RECYCLE);
        }

        // 3. 새로운 후보 생성 (하드 필터링 & 스코어링)
        List<RecommendationResDTO> newRecommendations = generateRecommendations(member, request);
        MatchType type = MatchType.DAILY_MATCH;

        // 결과가 비어있으면 랜덤 또는 재활용 시도 (로직: 필터 결과 0명 -> 랜덤)
        if (newRecommendations.isEmpty()) {
            // 랜덤 유저 백필 (generateRecommendations 내부에서 이미 처리됨)
        }

        // 큐에 저장
        // DB 저장은 일단 생략, 로직 반환에 집중

        return buildResponse(newRecommendations, 10, type); // 총 개수 모킹
    }

    private MatchListResDTO buildResponse(List<RecommendationResDTO> list, int remaining, MatchType type) {
        return MatchListResDTO.builder()
            .recommendations(list)
            .swipeInfo(MatchListResDTO.SwipeInfoDTO.builder()
                .remainingCount(remaining)
                .totalCount(10)
                .build())
            .matchType(type)
            .build();
    }

    private List<RecommendationResDTO> generateRecommendations(Member member, RecommendationReqDTO request) {
        // 1. 기 스와이프 유저 제외
        List<Long> excludedIds = memberLikeRepository.findSwipedMemberIdsByMember(member);

        // 2. 하드 필터 (QueryDSL)
        List<Member> candidates = matchRepository.findRecommendableMembers(member, request, excludedIds, 100);

        // 3. 소프트 스코어링 (점수 계산)
        // 3. 소프트 스코어링 (점수 계산)
        MemberLocation myLocation = memberLocationRepository.findByMember(member).orElse(null);
        // Preference myPreference = preferenceRepository.findByMember(member).orElse(null); // Removed

        List<MemberScore> scoredCandidates = candidates.stream()
            .map(candidate -> {
                double score = calculateScore(member, myLocation, candidate);
                return new MemberScore(candidate, score);
            })
            .sorted((p1, p2) -> Double.compare(p2.score, p1.score))
            .limit(10)
            .limit(10)
            .collect(Collectors.toList());

        // 10명 미만일 경우 랜덤 유저 백필 (엄격한 조건 적용)
        if (scoredCandidates.size() < 10) {
            int needed = 10 - scoredCandidates.size();
            List<Long> currentIds = scoredCandidates.stream().map(ms -> ms.member().getId()).collect(Collectors.toList());
            List<Long> allExcluded = new ArrayList<>(excludedIds);
            allExcluded.addAll(currentIds);

            List<Member> randomMembers = matchRepository.findRandomMembers(member, request, allExcluded, needed);

            randomMembers.forEach(rm -> scoredCandidates.add(new MemberScore(rm, 0.0)));
        }

        // 4. 큐에 저장
        List<RecommendationQueue> queueEntities = scoredCandidates.stream()
            .map(ms -> RecommendationQueue.builder()
                .member(member)
                .targetMember(ms.member())
                .score(ms.score())
                .isSwiped(false)
                .isRecycled(false) // 재활용 로직 필요 시 반영
                .build())
            .collect(Collectors.toList());

        recommendationQueueRepository.saveAll(queueEntities);

        return scoredCandidates.stream()
            .map(ms -> convertToDTO(ms.member))
            .collect(Collectors.toList());
    }

    private double calculateScore(Member me, MemberLocation myLoc, Member target) {
        double score = 0;

        // 1. 언어 점수 (최대 +5점)
        boolean meTargetMatch = me.getTargetLanguage() == target.getFirstLanguage();
        boolean targetTargetMatch = target.getTargetLanguage() == me.getFirstLanguage();

        if (meTargetMatch && targetTargetMatch) score += 5;
        else if (meTargetMatch || targetTargetMatch) score += 3;
        else if (me.getTargetLanguage() == target.getTargetLanguage()) score += 1;

        // 2. 성격 점수 (최대 +5점)
        if (me.getTargetLanguage() == target.getTargetLanguage()) score += 1;

        // 2. 성격 (PreferenceRepository 제한으로 인해 일단 생략)
        // if (myPref != null) {  }

        // 3. Interests (+N)

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
        if (myLoc != null) {
            MemberLocation targetLoc = memberLocationRepository.findByMember(target).orElse(null);
            if (targetLoc != null) {
                double dist = calculateDistance(myLoc.getLatitude(), myLoc.getLongitude(), targetLoc.getLatitude(), targetLoc.getLongitude());
                if (dist <= 50.0) score += 1;
            }
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
        return R * c;
    }

    private RecommendationResDTO convertToDTO(Member member) {
        int age = member.getBirthday() != null ? LocalDate.now().getYear() - member.getBirthday().getYear() + 1 : 0;

        // DTO 변환 시 Preference 정보 필요
        // (일단 생략)
        // PreferenceRepository 사용 안 함.
        RecommendationResDTO.PersonalityDTO personalityDTO = null;

        // 관심사 목록 조회
        List<String> interests = member.getInterestMembers().stream()
            .map(im -> im.getInterest().getType().name())
            .collect(Collectors.toList());

        return RecommendationResDTO.builder()
            .targetMemberId(member.getId())
            .nickname(member.getName())
            .age(age)
            .hometown(member.getHomeTown())
            .distance(0.0) // 위치 정보 로직 생략 (0.0 반환)
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
            .photoUrls(Collections.emptyList()) // 플레이스홀더
            .introduction(member.getBio())
            .build();
    }

    @Override
    @Transactional
    public SwipeResDTO swipe(Member member, SwipeReqDTO request) {
        Member target = memberRepository.findById(request.targetMemberId())
            .orElseThrow(() -> new IllegalArgumentException("Target member not found"));

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
}
