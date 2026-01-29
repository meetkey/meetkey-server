package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.match.enums.Action;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SwipeReqDTO(
    @NotNull Long targetMemberId,
    @NotNull Action action
) {
}
