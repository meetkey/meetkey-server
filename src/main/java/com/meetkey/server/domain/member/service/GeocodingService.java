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

    // 한국 범위 (위도 33~39, 경도 124~132)
    private static final double KOREA_MIN_LAT = 33.0;
    private static final double KOREA_MAX_LAT = 39.0;
    private static final double KOREA_MIN_LON = 124.0;
    private static final double KOREA_MAX_LON = 132.0;

    private boolean isInKorea(Double latitude, Double longitude) {
        return latitude >= KOREA_MIN_LAT && latitude <= KOREA_MAX_LAT
            && longitude >= KOREA_MIN_LON && longitude <= KOREA_MAX_LON;
    }

    public String getAddress(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }

        if (!isInKorea(latitude, longitude)) {
            log.debug("Skipping Kakao geocoding for non-Korea coordinates: lat={}, lon={}", latitude, longitude);
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
