package com.meetkey.server.domain.match.repository;

import com.meetkey.server.domain.match.dto.RecommendationReqDTO;
import com.meetkey.server.domain.member.entity.Member;

import java.util.List;

public interface MatchRepositoryCustom {
    List<Member> findRecommendableMembers(Member member, RecommendationReqDTO request, List<Long> excludedIds, int limit);

    List<Member> findRandomMembers(Member member, RecommendationReqDTO request, List<Long> excludedIds, int limit);
}
