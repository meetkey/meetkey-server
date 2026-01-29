package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.match.enums.Action;
import lombok.Builder;

@Builder
public record SwipeResDTO(
    Long targetMemberId,
    Action action
) {
}
