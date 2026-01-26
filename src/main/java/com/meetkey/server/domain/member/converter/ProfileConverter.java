package com.meetkey.server.domain.member.converter;

import com.meetkey.server.domain.member.entity.Interest;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.enums.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

        // 모든 관심사를 카테고리별로 분류
        Map<InterestCategory, List<InterestType>> groupedMap = Arrays.stream(InterestType.values())
                .collect(Collectors.groupingBy(InterestType::getCategory));

        // 카테고리 Enum 순서대로 DTO 생성
        List<InterestCategoryDetail> categoryDetails = Arrays.stream(InterestCategory.values())
                .map(category -> {
                    // 해당 카테고리에 속하는 관심사 리스트 가져오기
                    List<InterestType> types = groupedMap.get(category);

                    // 관심사들을 InterestItem DTO로 변환
                    List<InterestItem> items = types.stream()
                            .map(type -> InterestItem.builder()
                                    .code(type)       // TRAVEL
                                    .name(type.getName()) // "여행"
                                    .build())
                            .toList();

                    // CategoryDetail DTO 생성
                    return InterestCategoryDetail.builder()
                            .category(category.getDescription()) // "일상, 라이프스타일"
                            .items(items)
                            .build();
                })
                .toList();

        // 최종 변환
        return InterestCategoryResponse.builder()
                .categories(categoryDetails)
                .build();
    }

    public PersonalityCategoryResponse toPersonalityCategoryResponse() {
        List<PersonalityCategoryDetail> categories = new ArrayList<>();

        categories.add(createCategory("사회적 에너지 성향", SocialType.values()));
        categories.add(createCategory("선호하는 만남 방식", MeetingType.values()));
        categories.add(createCategory("대화 시작 스타일", ChatType.values()));
        categories.add(createCategory("친구 유형 선호도", FriendType.values()));
        categories.add(createCategory("관계 목적", RelationType.values()));

        return PersonalityCategoryResponse.builder()
                .categories(categories)
                .build();

    }

    // Enum 배열 -> CategoryDetail DTO 변환
    private static PersonalityCategoryDetail createCategory(String title, Enum<?>[] enums) {
        List<String> options = Arrays.stream(enums)
                .map(Enum::name) // "EXTROVERT" 같이 영어 코드만 추출
                .toList();

        return PersonalityCategoryDetail.builder()
                .title(title)
                .options(options)
                .build();
    }
}
