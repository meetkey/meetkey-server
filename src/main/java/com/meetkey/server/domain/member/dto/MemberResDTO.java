package com.meetkey.server.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class MemberResDTO {

    @Builder
    public record Block(
            @NotNull Long fromMemberId,
            @NotNull Long toMemberId
    ){}
}
