package com.meetkey.server.domain.match.dto;

import com.meetkey.server.domain.member.enums.Gender;
import com.meetkey.server.domain.member.enums.HomeTown;
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
        com.meetkey.server.domain.member.enums.Language language,
        com.meetkey.server.domain.member.enums.Level level
    ) {
    }

    @Builder
    public record PersonalityDTO(
        com.meetkey.server.domain.member.enums.SocialType socialType,
        com.meetkey.server.domain.member.enums.MeetingType meetingType,
        com.meetkey.server.domain.member.enums.ChatType chatType,
        com.meetkey.server.domain.member.enums.FriendType friendType,
        com.meetkey.server.domain.member.enums.RelationType relationType
    ) {
    }
}
