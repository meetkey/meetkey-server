package com.meetkey.server.global.security.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoMapDto {
    private Meta meta;
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Meta {
        private int totalCount;
    }

    @Getter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Document {
        private String regionType;
        private String addressName;
        @com.fasterxml.jackson.annotation.JsonProperty("region_1depth_name")
        private String region1depthName; // 시/도
        @com.fasterxml.jackson.annotation.JsonProperty("region_2depth_name")
        private String region2depthName; // 시/군/구
        @com.fasterxml.jackson.annotation.JsonProperty("region_3depth_name")
        private String region3depthName; // 읍/면/동
        @com.fasterxml.jackson.annotation.JsonProperty("region_4depth_name")
        private String region4depthName;
        private String code;
        private double x;
        private double y;
    }
}
