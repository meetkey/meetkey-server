package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.member.enums.HomeTown;
import com.meetkey.server.domain.member.enums.InterestType;
import com.meetkey.server.domain.member.enums.Language;
import com.meetkey.server.domain.member.enums.Level;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationReqDTO(
    List<InterestType> interests,
    // 성격 필터는 Preference 제약으로 인해 생략
    List<HomeTown> homeTown,
    List<Language> nativeLanguage,
    List<Language> targetLanguage,
    List<Level> targetLanguageLevel,
    Integer minAge,
    Integer maxAge,
    Double latitude,
    Double longitude,
    Double maxDistance // km 단위
) {
}
