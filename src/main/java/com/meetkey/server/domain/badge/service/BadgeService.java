package com.meetkey.server.domain.badge.service;

import com.meetkey.server.domain.badge.converter.BadgeConverter;
import com.meetkey.server.domain.badge.entity.PointHistory;
import com.meetkey.server.domain.badge.enums.ReasonType;
import com.meetkey.server.domain.badge.respository.PointHistoryRepository;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.meetkey.server.domain.badge.dto.BadgeResDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeService {

    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final BadgeConverter badgeConverter;

    // 뱃지 정보 조회 (내역 포함)
    @Transactional(readOnly = true)
    public BadgeResponse getBadgeDetail(Long memberId) {
        Member member = getMember(memberId);

        int totalScore = pointHistoryRepository.calculateTotalScore(member);

        List<PointHistory> histories = pointHistoryRepository.findAllByMemberOrderByCreatedAtDesc(member);

        return badgeConverter.toBadgeResponseDetail(totalScore, histories);
    }

    // 뱃지 정보 조회(내역 미포함)
    @Transactional(readOnly = true)
    public BadgeResponse getBadgeSummary(Long memberId) {
        Member member = getMember(memberId);

        int totalScore = pointHistoryRepository.calculateTotalScore(member);

        return badgeConverter.toBadgeResponseSummary(totalScore);
    }

    // 점수 부여
    public void rewardPoints(Long memberId, ReasonType reasonType) {
        Member member = getMember(memberId);

        if (pointHistoryRepository.existsByMemberAndReasonType(member, reasonType)) {
            return;
        }

        int currentTotalScore = pointHistoryRepository.calculateTotalScore(member);

        // 100점 이상이라면 지급 X
        if (currentTotalScore >= 100) {
            return;
        }

        int pointsToAdd = reasonType.getDefaultScore();
        int potentialScore = currentTotalScore + pointsToAdd;

        // 더해서 100점 초과라면 일부만 지급
        if (potentialScore > 100) {
            pointsToAdd = 100 - currentTotalScore;
        }

        if (pointsToAdd > 0) {
            PointHistory pointHistory = PointHistory.builder()
                    .member(member)
                    .reasonType(reasonType)
                    .changeAmount(reasonType.getDefaultScore())
                    .build();
            pointHistoryRepository.save(pointHistory);
        }

    }

    // 본인 인증
    public void checkAuthentication(Long memberId) {
        Member member = getMember(memberId);

        if (Boolean.TRUE.equals(member.isVerified())) {
            rewardPoints(member.getId(), ReasonType.AUTH);
        }
    }

    // 프로필 작성
    public void checkProfileCompletion(Long memberId) {
        Member member = getMember(memberId);

        boolean isComplete =
                member.getLocation() != null &&
                member.getFirstLanguage() != null &&
                member.getTargetLanguage() != null &&
                member.getTargetLanguageLevel() != null;

        if (isComplete) {
            rewardPoints(member.getId(), ReasonType.PROFILE);
        }
    }

    // 긍적적인 평가
    public void checkPositiveEvaluation(Long memberId) {
        Member member = getMember(memberId);

        if (member.getRecommendCount() >= 10) {
            rewardPoints(member.getId(), ReasonType.POSITIVE);
        }
    }


    // 사용자 찾기 메소드
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }
}
