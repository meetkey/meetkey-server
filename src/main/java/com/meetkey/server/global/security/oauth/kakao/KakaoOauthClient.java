package com.meetkey.server.global.security.oauth.kakao;

import com.meetkey.server.global.config.FeignConfig;
import com.meetkey.server.global.config.OauthConfig;
import com.meetkey.server.global.security.oauth.dto.OidcDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "KakaoAuthClient",
        url = "https://kauth.kakao.com",
        configuration = {OauthConfig.class, FeignConfig.class}
)
public interface KakaoOauthClient {
    @Cacheable(cacheNames = "KakaoOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    OidcDTO.OIDCPublicKeys getKakaoOIDCOpenKeys();
}
