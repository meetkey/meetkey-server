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

    // 뱃지 정보 조회
    @Transactional(readOnly = true)
    public BadgeResponse getBadgeInfo(Long memberId) {
        Member member = getMember(memberId);

        int totalScore = pointHistoryRepository.calculateTotalScore(member);

        List<PointHistory> histories = pointHistoryRepository.findAllByMemberOrderByCreatedAtDesc(member);

        return badgeConverter.toBadgeResponse(totalScore, histories);
    }

    // 점수 부여
    public void rewardPoints(Long memberId, ReasonType reasonType) {
        Member member = getMember(memberId);

        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .reasonType(reasonType)
                .changeAmount(reasonType.getDefaultScore())
                .build();

        pointHistoryRepository.save(pointHistory);
    }


    // 사용자 찾기 메소드
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }
}
