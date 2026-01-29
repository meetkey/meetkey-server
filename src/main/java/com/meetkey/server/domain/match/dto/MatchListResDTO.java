package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.match.enums.MatchType;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchListResDTO(
    List<RecommendationResDTO> recommendations,
    SwipeInfoDTO swipeInfo,
    MatchType matchType
) {
    @Builder
    public record SwipeInfoDTO(
        int remainingCount,
        int totalCount
    ) {
    }
}
