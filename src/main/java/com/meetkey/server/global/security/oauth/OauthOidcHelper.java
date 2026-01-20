package com.meetkey.server.global.security.oauth;

import com.meetkey.server.global.security.oauth.dto.OidcDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthOidcHelper {
    private final JwtOidcProvider jwtOIDCProvider;

    private String getKidFromIdToken(String token, String iss, String aud, String nonce){
        return jwtOIDCProvider.getKidFromIdTokenHeader(token, iss, aud, nonce);
    }

    public OidcDTO.OIDCDecodePayload getPayloadFromIdToken(
            String token, String iss, String aud, String nonce, OidcDTO.OIDCPublicKeys response
    ){
        String kid = getKidFromIdToken(token, iss, aud, nonce); // ID 토큰의 kid 확인

        // kid 에 맞는 공개키를 가져옴
        OidcDTO.OIDCPublicKey publicKey  =
                response.keys().stream()
                        .filter(o -> o.kid().equals(kid))
                        .findFirst()
                        .orElseThrow();

        return jwtOIDCProvider.getOIDCTokenBody(token, publicKey.n(), publicKey.e());
    }

}
