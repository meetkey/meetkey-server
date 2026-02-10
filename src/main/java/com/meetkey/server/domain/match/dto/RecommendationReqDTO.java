package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.member.enums.HomeTown;
import com.meetkey.server.domain.member.enums.InterestType;
import com.meetkey.server.domain.member.enums.Language;
import com.meetkey.server.domain.member.enums.Level;
import com.meetkey.server.domain.member.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationReqDTO(
    @Schema(description = "관심사 리스트 (없을 시 전체)", example = "[\"HOBBY\", \"WORKOUT\"]")
    List<InterestType> interests,
    @Schema(description = "국적 필터 (없을 시 전체)", example = "[\"KOREA\", \"JAPAN\"]")
    List<HomeTown> homeTown,
    @Schema(description = "모국어 필터", example = "[\"KOREAN\"]")
    List<Language> nativeLanguage,
    @Schema(description = "학습 언어 필터", example = "[\"JAPANESE\"]")
    List<Language> targetLanguage,
    @Schema(description = "학습 언어 레벨 필터", example = "[\"BEGINNER\"]")
    List<Level> targetLanguageLevel,

    @Schema(description = "성향(Personality) 태그 리스트 (예: [\"EXTROVERT\", \"GROUP\"])", example = "[\"EXTROVERT\", \"GROUP\"]")
    List<String> personalities,

    @Schema(description = "최소 나이 (18세 이상)", example = "20")
    @Min(value = 18, message = "최소 나이는 18세 이상이어야 합니다.")
    Integer minAge,

    @Schema(description = "최대 나이 (50세 이하)", example = "30")
    @Max(value = 50, message = "최대 나이는 50세 이하여야 합니다.")
    Integer maxAge,

    @Schema(description = "현재 위도", example = "37.5665")
    Double latitude,

    @Schema(description = "현재 경도", example = "126.9780")
    Double longitude,

    @Schema(description = "최대 검색 거리(km) (0 이상)", example = "50")
    @Max(value = 400, message = "최대 검색 거리는 400km 이하여야 합니다.")
    @PositiveOrZero(message = "거리는 0 이상이어야 합니다.")
    Double maxDistance // km 단위
) {
}
