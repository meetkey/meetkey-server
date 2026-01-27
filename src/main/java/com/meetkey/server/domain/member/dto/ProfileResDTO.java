package com.meetkey.server.domain.member.dto;

import com.meetkey.server.domain.member.enums.*;
import lombok.Builder;

import java.util.List;

public class ProfileResDTO {

    @Builder
    public record ProfileResponse(
            Long memberId,
            String name,
            int age,
            String location,
            String bio,
            Language first,
            Language target,
            Level level
    ) {}

    @Builder
    public record InterestResponse(
            List<InterestType> interests
    ) {}

    @Builder
    public record InterestCategoryResponse(
            List<InterestCategoryDetail> categories
    ) {}

    @Builder
    public record InterestCategoryDetail(
            String category,
            List<InterestItem> items
    ) {}

    @Builder
    public record InterestItem(
            InterestType code,
            String name
    ) {}


    @Builder
    public record PersonalityCategoryResponse(
            List<PersonalityCategoryDetail> categories
    ) {}

    @Builder
    public record PersonalityCategoryDetail(
            String title,
            List<String> options
    ) {}

    @Builder
    public record PersonalityUpdateResponse(
            SocialType socialType,
            MeetingType meetingType,
            ChatType chatType,
            FriendType friendType,
            RelationType relationType
    ) {}

}
