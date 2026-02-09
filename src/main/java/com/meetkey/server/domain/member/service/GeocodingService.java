package com.meetkey.server.domain.member.service;

import com.meetkey.server.global.security.oauth.dto.KakaoMapDto;
import com.meetkey.server.global.security.oauth.kakao.KakaoMapClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final KakaoMapClient kakaoMapClient;

    @Value("${kakao.map-api-key}")
    private String kakaoMapApiKey;

    public String getAddress(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }

        try {
            String authorization = "KakaoAK " + kakaoMapApiKey;

            KakaoMapDto response = kakaoMapClient.getRegionCode(authorization, longitude, latitude);

            if (response != null && response.getDocuments() != null && !response.getDocuments().isEmpty()) {
                KakaoMapDto.Document doc = response.getDocuments().stream()
                    .filter(d -> "H".equals(d.getRegionType()))
                    .findFirst()
                    .orElse(response.getDocuments().get(0));

                String city = doc.getRegion1depthName();
                String district = doc.getRegion2depthName();

                return "대한민국, " + city;
            }
        } catch (Exception e) {
            log.error("Geocoding failed for lat: {}, lon: {}", latitude, longitude, e);
        }
        return null;
    }
}
