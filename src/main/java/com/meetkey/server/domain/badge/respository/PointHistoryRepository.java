package com.meetkey.server.domain.badge.respository;

import com.meetkey.server.domain.badge.entity.PointHistory;
import com.meetkey.server.domain.badge.enums.ReasonType;
import com.meetkey.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findAllByMemberOrderByCreatedAtDesc(Member member);

    // 총점 계산
    @Query("SELECT coalesce(sum(p.changeAmount), 0) FROM PointHistory p where p.member = :member")
    int calculateTotalScore(@Param("member") Member member);

    // 특정 사유로 이미 점수를 받았는지 확인
    boolean existsByMemberAndReasonType(Member member, ReasonType reasonType);
}
