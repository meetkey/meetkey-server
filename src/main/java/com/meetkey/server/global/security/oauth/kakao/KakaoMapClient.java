package com.meetkey.server.global.security.oauth.kakao;

import com.meetkey.server.global.config.FeignConfig;
import com.meetkey.server.global.config.OauthConfig;
import com.meetkey.server.global.security.oauth.dto.KakaoMapDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "KakaoMapClient",
    url = "https://dapi.kakao.com",
    configuration = {OauthConfig.class, FeignConfig.class}
)
public interface KakaoMapClient {
    @GetMapping("/v2/local/geo/coord2regioncode.json")
    KakaoMapDto getRegionCode(
        @RequestHeader("Authorization") String authorization,
        @RequestParam("x") Double longitude,
        @RequestParam("y") Double latitude
    );
}
