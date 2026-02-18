package com.meetkey.server.global.security.oauth.kakao;

import com.meetkey.server.global.config.FeignConfig;
import com.meetkey.server.global.config.OauthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "KakaoApiClient",
        url = "https://kapi.kakao.com",
        configuration = {OauthConfig.class, FeignConfig.class}
)
public interface KakaoApiClient {
    @PostMapping("/v1/user/unlink")
    void unlink(
            @RequestHeader("Authorization") String adminKey,
            @RequestParam("target_id_type") String targetIdType,
            @RequestParam("target_id") Long targetId
    );
}