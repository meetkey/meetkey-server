package com.meetkey.server.domain.member.dto;

import com.meetkey.server.domain.member.enums.*;
import lombok.Builder;

import java.util.List;

public class ProfileResDTO {

    @Builder
    public record ProfileUpdateResponse(
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

    @Builder
    public record MyProfileResponse(
            // 헤더부분
            Long memberId,
            String name,
            Language first,
            Language target,
            int age,
            String profileImage,

            // 평판
            int recommendCount,
            int notRecommendCount,

            // 관심사
            List<String> interests,

            // 성향
            PersonalityUpdateResponse personalities,

            String bio
    ) {}

}
