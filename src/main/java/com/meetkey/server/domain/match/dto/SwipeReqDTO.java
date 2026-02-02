package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.match.enums.Action;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SwipeReqDTO(
    @Schema(description = "대상 유저 ID", example = "10")
    @NotNull(message = "타겟 유저 ID는 필수입니다.")
    Long targetMemberId,

    @Schema(description = "LIKE 또는 DISLIKE", example = "LIKE")
    @NotNull(message = "Action(LIKE/DISLIKE)은 필수입니다.")
    Action action
) {
}
