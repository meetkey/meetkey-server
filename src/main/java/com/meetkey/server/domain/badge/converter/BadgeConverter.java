package com.meetkey.server.domain.badge.converter;

import com.meetkey.server.domain.badge.dto.BadgeResDTO;
import com.meetkey.server.domain.badge.entity.Badge;
import com.meetkey.server.domain.badge.entity.PointHistory;
import com.meetkey.server.domain.badge.enums.BadgeLevel;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.meetkey.server.domain.badge.dto.BadgeResDTO.*;

@Component
public class BadgeConverter {

    public BadgeResponse toBadgeResponse(int totalScore, List<PointHistory> histories) {

        BadgeLevel badgeLevel = BadgeLevel.fromScore(totalScore);

        List<HistoryDetail> details = histories.stream()
                .map(h -> HistoryDetail.builder()
                        .reason(h.getReasonType().getDescription())
                        .amount(h.getChangeAmount())
                        .date(h.getCreatedAt())
                        .build())
                .toList();

        return BadgeResponse.builder()
                .badgeName(badgeLevel.getName())
                .totalScore(totalScore)
                .histories(details)
                .build();

    }

}
