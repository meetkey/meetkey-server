package com.meetkey.server.domain.member.dto;

import com.meetkey.server.domain.member.enums.InterestCategory;
import com.meetkey.server.domain.member.enums.InterestType;
import lombok.Builder;

import java.util.List;

public class ProfileResDTO {

    @Builder
    public record ProfileResponse(
            Long memberId,
            String name,
            int age,
            String location,
            String bio
    ) {}

    @Builder
    public record InterestResponse(
            List<InterestType> interests
    ) {}

    @Builder
    public record InterestCategoryResponse(
            List<CategoryDetail> categories
    ) {}

    @Builder
    public record CategoryDetail(
            String category,
            List<InterestItem> items
    ) {}

    @Builder
    public record InterestItem(
            InterestType code,
            String name
    ) {}

}
