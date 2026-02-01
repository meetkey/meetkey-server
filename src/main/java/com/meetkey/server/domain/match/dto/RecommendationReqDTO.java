package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.member.enums.HomeTown;
import com.meetkey.server.domain.member.enums.InterestType;
import com.meetkey.server.domain.member.enums.Language;
import com.meetkey.server.domain.member.enums.Level;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
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

    @Min(value = 18, message = "최소 나이는 18세 이상이어야 합니다.")
    Integer minAge,

    @Max(value = 100, message = "최대 나이는 100세 이하여야 합니다.")
    Integer maxAge,

    Double latitude,

    Double longitude,

    @PositiveOrZero(message = "거리는 0 이상이어야 합니다.")
    Double maxDistance // km 단위
) {
}
