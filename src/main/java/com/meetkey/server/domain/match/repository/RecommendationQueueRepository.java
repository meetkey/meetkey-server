package com.meetkey.server.domain.match.repository;

import com.meetkey.server.domain.match.entity.RecommendationQueue;
import com.meetkey.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationQueueRepository extends JpaRepository<RecommendationQueue, Long> {

    Optional<RecommendationQueue> findByMemberAndTargetMember(Member member, Member targetMember);

    void deleteByMemberAndIsSwipedFalse(Member member);
}
