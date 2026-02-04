package com.meetkey.server.global.security.oauth.apple;

import com.meetkey.server.global.config.OauthConfig;
import com.meetkey.server.global.security.oauth.dto.OidcDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "AppleOauthClient",
        url = "https://appleid.apple.com",
        configuration = OauthConfig.class
)
public interface AppleOauthClient {
    @Cacheable(cacheNames = "AppleOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/auth/keys")
    OidcDTO.OIDCPublicKeys getAppleOIDCOpenKeys();
}
