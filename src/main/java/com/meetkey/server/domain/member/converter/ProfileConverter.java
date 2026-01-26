package com.meetkey.server.domain.member.converter;

import com.meetkey.server.domain.member.entity.Interest;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.enums.InterestCategory;
import com.meetkey.server.domain.member.enums.InterestType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Component
public class ProfileConverter {

    public ProfileResponse toProfileRes(Member member) {
        return ProfileResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .location(member.getLocation())
                .bio(member.getBio())
                .build();
    }

    public InterestResponse toInterestResponse(List<Interest> interests) {
        List<InterestType> types = interests.stream()
                .map(Interest::getType)
                .collect(Collectors.toList());

        return InterestResponse.builder()
                .interests(types)
                .build();
    }

    public InterestCategoryResponse toCategoryResponse() {
        // 모든 관심사를 카테고리별로 묶기
        Map<InterestCategory, List<InterestType>> groupedMap = Arrays.stream(InterestType.values())
                .collect(Collectors.groupingBy(InterestType::getCategory));

        List<CategoryDetail> categoryDetails = Arrays.stream(InterestCategory.values())
                .map(category -> {
                    // 해당 카테고리에 속하는 관심사 가져오기
                    List<InterestType> types = groupedMap.get(category);

                    // 관심사들 InterestItem DTO로 변환
                    List<InterestItem> items = types.stream()
                            .map(type -> InterestItem.builder()
                                    .code(type)
                                    .name(type.getName())
                                    .build())
                            .toList();

                    // CategoryDetail DTO 생성
                    return CategoryDetail.builder()
                            .category(category.getDescription())
                            .items(items)
                            .build();
                })
                .toList();
        // 최종 반환
        return InterestCategoryResponse.builder()
                .categories(categoryDetails)
                .build();

    }
}
