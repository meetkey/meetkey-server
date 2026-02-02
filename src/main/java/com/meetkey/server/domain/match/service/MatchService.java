package com.meetkey.server.domain.match.service;

import com.meetkey.server.domain.match.dto.MatchListResDTO;
import com.meetkey.server.domain.match.dto.RecommendationReqDTO;
import com.meetkey.server.domain.match.dto.SwipeReqDTO;
import com.meetkey.server.domain.match.dto.SwipeResDTO;

public interface MatchService {
    MatchListResDTO getRecommendations(Long memberId, RecommendationReqDTO request);

    SwipeResDTO swipe(Long memberId, SwipeReqDTO request);
}
