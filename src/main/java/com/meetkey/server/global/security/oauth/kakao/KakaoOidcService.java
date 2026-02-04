package com.meetkey.server.global.security.oauth.kakao;

import com.meetkey.server.global.security.oauth.dto.OidcDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOidcService {
    private final KakaoOauthClient kakaoOauthClient;

    @Cacheable(cacheNames = "KakaoOICD", cacheManager = "oidcCacheManager")
    public OidcDTO.OIDCPublicKeys getKakaoOIDCOpenKeys(){
        return kakaoOauthClient.getKakaoOIDCOpenKeys();
    };
}
