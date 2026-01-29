package com.meetkey.server.domain.match.repository;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.match.entity.RecommendationQueue;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationQueueRepository extends JpaRepository<RecommendationQueue, Long> {
    List<RecommendationQueue> findAllByMemberAndIsSwipedFalse(Member member, Sort sort);
    
    // For cleaning up old queue if needed or checking count
    long countByMemberAndIsSwipedFalse(Member member);
    
    java.util.Optional<RecommendationQueue> findByMemberAndTargetMember(Member member, Member targetMember);

    List<RecommendationQueue> findTop10ByMemberAndIsSwipedTrueAndIsRecycledFalseOrderByUpdateAtDesc(Member member);
}
