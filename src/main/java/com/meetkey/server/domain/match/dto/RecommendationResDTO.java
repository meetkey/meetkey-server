package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.member.enums.*;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResDTO(
    Long targetMemberId,
    String nickname,
    int age,
    HomeTown hometown,
    double distance,
    Gender gender,
    LanguageDTO nativeLanguage,
    LanguageDTO targetLanguage,
    List<String> interests,
    PersonalityDTO personality,
    List<String> photoUrls,
    String introduction
) {
    @Builder
    public record LanguageDTO(
        Language language,
        Level level
    ) {
    }

    @Builder
    public record PersonalityDTO(
        SocialType socialType,
        MeetingType meetingType,
        ChatType chatType,
        FriendType friendType,
        RelationType relationType
    ) {
    }
}
