package com.meetkey.server.global.security.jwt.dto;

import lombok.Builder;

public class JwtResDTO {
    /*
     * 서비스 내부용 (service <-> controller 간의 dto)
     */
    @Builder
    public record JwtResponse(
            String accessToken,
            String refreshToken,
            Long memberId,
            Boolean isNewMember
    ){}

    @Builder
    public record LoginResponse(
            Long memberId,
            Boolean isNewMember
    ){}

    @Builder
    public record RefreshTokenResponse(
            Long memberId,
            String refreshToken,
            String expiration
    ){}
}
