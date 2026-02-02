package com.meetkey.server.domain.member.controller;

import com.meetkey.server.domain.member.service.ProfileService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<BasicResponse<ProfileUpdateResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ProfileUpdateRequest request
    ) {

        Long memberId = customUserDetails.getMemberId();
        ProfileUpdateResponse response = profileService.updateProfile(memberId, request);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "위치 정보 수정 API", description = "사용자의 위도, 경도를 수정합니다.")
    @PatchMapping("/me/location")
    public ResponseEntity<BasicResponse<String>> updateLocation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody LocationUpdateRequest request
    ) {
        Long memberId = customUserDetails.getMemberId();
        profileService.updateLocation(memberId, request);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, "위치 정보가 수정되었습니다."));
    }

    @Operation(summary = "프로필 수정 시 조회 API", description = "이름, 나이, 위치, 한줄 소개를 조회합니다.")
    @GetMapping("/me/profile")
    public ResponseEntity<BasicResponse<ProfileUpdateResponse>> getProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        Long memberId = customUserDetails.getMemberId();
        ProfileUpdateResponse response = profileService.getMyUpdateProfile(memberId);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "관심사 수정 API", description = "온보딩 및 관심사 수정 시 관심사를 수정합니다.")
    @PutMapping("/me/interest")
    public ResponseEntity<BasicResponse<InterestResponse>> updateInterest(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody InterestUpdateRequest request
    ) {

        Long memberId = customUserDetails.getMemberId();
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

    @Operation(summary = "성향 수정 API", description = "온보딩 및 성향 수정 시 성향을 수정합니다.")
    @PutMapping("/me/personality")
    public ResponseEntity<BasicResponse<PersonalityUpdateResponse>> updatePersonality(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody PersonalityUpdateRequest request
    ) {
        Long memberId = customUserDetails.getMemberId();

        PersonalityUpdateResponse response = profileService.updatePersonality(memberId, request);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "프로필 조회 API", description = "내 프로필을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<BasicResponse<MyProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long memberId = customUserDetails.getMemberId();

        MyProfileResponse response = profileService.getMyProfile(memberId);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "상대방 프로필 조회 API", description = "상대방 프로필을 조회합니다.")
    @GetMapping("/{targetId}")
    public ResponseEntity<BasicResponse<OtherProfileResponse>> getOtherProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long targetId
    ) {
        Long myId = customUserDetails.getMemberId();

        OtherProfileResponse response = profileService.getOtherProfile(myId, targetId);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "추천/비추천 토글 API", description = "프로필에서 추천/비추천 토글기능입니다.")
    @PostMapping("/evaluation/toggle")
    public ResponseEntity<BasicResponse<String>> toggleEvaluation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody EvaluationRequest request
    ) {
        Long memberId = customUserDetails.getMemberId();
        profileService.toggleEvaluation(memberId, request.targetMemberId(), request.type());
        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, "평가가 반영되었습니다."));
    }
}

