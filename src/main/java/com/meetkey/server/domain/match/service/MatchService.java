package com.meetkey.server.domain.match.service;

import com.meetkey.server.domain.match.dto.MatchListResDTO;
import com.meetkey.server.domain.match.dto.RecommendationReqDTO;
import com.meetkey.server.domain.match.dto.SwipeReqDTO;
import com.meetkey.server.domain.match.dto.SwipeResDTO;
import com.meetkey.server.domain.member.entity.Member;

public interface MatchService {
    MatchListResDTO getRecommendations(Member member, RecommendationReqDTO request);

    SwipeResDTO swipe(Member member, SwipeReqDTO request);
}
