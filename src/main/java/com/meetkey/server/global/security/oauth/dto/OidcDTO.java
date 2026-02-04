package com.meetkey.server.global.security.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

public class OidcDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OIDCDecodePayload {
        private String iss;
        private Set<String> aud;
        private String sub;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OIDCPublicKey {
        private String kid;
        private String kty;
        private String alg;
        private String use;
        private String n;
        private String e;
    }

    // 인증서버가 ID 토큰 서명 시 사용한 공개키 목록을 담은 JWK 배열
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OIDCPublicKeys {
        private List<OIDCPublicKey> keys;
    }
}
