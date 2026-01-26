package com.meetkey.server.domain.member.dto;

import lombok.Builder;

public class ProfileResDTO {

    @Builder
    public record ProfileUpdateResponse(
            Long memberId,
            String name,
            int age,
            String location,
            String bio
    ) {}

}
