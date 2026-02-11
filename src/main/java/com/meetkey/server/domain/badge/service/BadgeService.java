package com.meetkey.server.domain.badge.service;

import com.meetkey.server.domain.badge.converter.BadgeConverter;
import com.meetkey.server.domain.badge.entity.Badge;
import com.meetkey.server.domain.badge.entity.PointHistory;
import com.meetkey.server.domain.badge.enums.ReasonType;
import com.meetkey.server.domain.badge.respository.BadgeRepository;
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
    private final BadgeRepository badgeRepository;
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
    public void rewardPoints(Member member, ReasonType reasonType) {
        int amount = reasonType.getDefaultScore();

        Badge badge = badgeRepository.findByMember(member).orElseThrow(
                () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        int currentScore = badge.getTotal_score();

        int potetialScore = currentScore + amount;

        int potentialScore = currentScore + amount;

        if (potentialScore > 100) {
            amount = 100 - currentScore; // 100점까지만 채움
        } else if (potentialScore < 0) {
            amount = -currentScore;
        }

        if (amount == 0) return; // 변동 없으면 종료

        badge.addScore(amount); // Badge 엔티티 업데이트

        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .reasonType(reasonType)
                .changeAmount(amount)
                .build();
        pointHistoryRepository.save(pointHistory);
    }

    // 본인 인증
    public void checkAuthentication(Long memberId) {
        Member member = getMember(memberId);

        if (Boolean.TRUE.equals(member.isVerified()) && !hasAlreadyReceived(member, ReasonType.AUTH)) {
            rewardPoints(member, ReasonType.AUTH);
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

        if (isComplete && !hasAlreadyReceived(member, ReasonType.PROFILE)) {
            rewardPoints(member, ReasonType.PROFILE);
        }
    }

    // 긍적적인 평가
    public void checkPositiveEvaluation(Long memberId) {
        Member member = getMember(memberId);

        if (member.getRecommendCount() >= 10 && !hasAlreadyReceived(member, ReasonType.PROFILE)) {
            rewardPoints(member, ReasonType.POSITIVE);
        }
    }

    // 미션용 지급
    public void rewardRepeatable(Long memberId, ReasonType reasonType) {
        Member member = getMember(memberId);
        rewardPoints(member, reasonType);
    }

    private boolean hasAlreadyReceived(Member member, ReasonType reasonType) {
        return pointHistoryRepository.existsByMemberAndReasonType(member, reasonType);
    }

    // 사용자 찾기 메소드
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }
}
