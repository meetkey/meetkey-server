package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.match.enums.Action;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SwipeReqDTO(
    @NotNull(message = "타겟 유저 ID는 필수입니다.")
    Long targetMemberId,

    @NotNull(message = "Action(LIKE/DISLIKE)은 필수입니다.")
    Action action
) {
}
