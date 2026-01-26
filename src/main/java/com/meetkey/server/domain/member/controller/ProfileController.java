package com.meetkey.server.domain.member.controller;

import com.meetkey.server.domain.member.service.ProfileService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.meetkey.server.domain.member.dto.ProfileReqDTO.*;
import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "프로필 수정 API", description = "사용자의 위치, 한줄 소개를 변경합니다.")
    @PatchMapping("/me/profile")
    public ResponseEntity<BasicResponse<ProfileResponse>> updateProfile(
            @RequestAttribute("memberId") Long memberId,
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileResponse response = profileService.updateProfile(memberId, request);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "프로필 수정 시 조회 API", description = "이름, 나이, 위치, 한줄 소개를 조회합니다.")
    @GetMapping("/me/profile")
    public ResponseEntity<BasicResponse<ProfileResponse>> getProfile(
            @RequestAttribute("memberId") Long memberId
    ) {
        ProfileResponse response = profileService.getMyProfile(memberId);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "관심사 수정 API", description = "온보딩 및 관심사 수정 시 관심사를 수정합니다.")
    @PutMapping("/me/interest")
    public ResponseEntity<BasicResponse<InterestResponse>> updateInterest(
            @RequestAttribute("memberId") Long memberId,
            @RequestBody InterestUpdateRequest request
    ) {

        InterestResponse response = profileService.updateInterests(memberId, request.interests());

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "관심사 조회 API", description = "온보딩 및 관심사 수정 시 관심사들을 조회합니다.")
    @GetMapping("/me/interest")
    public ResponseEntity<BasicResponse<InterestCategoryResponse>> getInterestCategory() {
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, profileService.getAllInterests()));
    }

    @Operation(summary = "성향 조회 API", description = "온본딩 및 성향 수정 시 성향들을 조회합니다.")
    @GetMapping("/me/personality")
    public ResponseEntity<BasicResponse<PersonalityCategoryResponse>> getPersonalityCategory() {
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, profileService.getAllPersonality()));
    }

}

