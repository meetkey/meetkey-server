package com.meetkey.server.domain.badge.dto;

import com.meetkey.server.domain.badge.entity.PointHistory;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class BadgeResDTO {

    @Builder
    public record BadgeResponse(
            String badgeName,
            int totalScore,
            List<HistoryDetail> histories
    ) {}

    @Builder
    public record HistoryDetail(
            String reason,
            int amount,
            LocalDateTime date
    ) {}
}
