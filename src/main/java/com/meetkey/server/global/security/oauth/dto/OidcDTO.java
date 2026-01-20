package com.meetkey.server.global.security.oauth.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

public class OidcDTO {
    @Builder
    public record OIDCDecodePayload (
        String iss,
        Set<String> aud,
        String sub
    ){}

    @Builder
    public record OIDCPublicKey (
        // JWK
        String kid,
        String alg,
        String n,
        String e
    ){}

    @Builder
    public record OIDCPublicKeys(
        List<OIDCPublicKey> keys  // 인증서버가 ID 토큰 서명 시 사용한 공개키 목록을 담은 JWK 배열
    ){}
}
