package com.meetkey.server.domain.match.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationReqDTO(
    List<com.meetkey.server.domain.member.enums.InterestType> interests,
    // 성격 필터는 Preference 제약으로 인해 생략
    List<com.meetkey.server.domain.member.enums.HomeTown> homeTown,
    List<com.meetkey.server.domain.member.enums.Language> nativeLanguage,
    List<com.meetkey.server.domain.member.enums.Language> targetLanguage,
    List<com.meetkey.server.domain.member.enums.Level> targetLanguageLevel,
    Integer minAge,
    Integer maxAge,
    Double latitude,
    Double longitude,
    Double maxDistance // km 단위
) {
}
