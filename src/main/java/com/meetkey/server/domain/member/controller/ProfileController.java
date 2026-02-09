package com.meetkey.server.domain.member.controller;

import com.meetkey.server.domain.member.dto.ProfileReqDTO;
import com.meetkey.server.domain.member.dto.ProfileResDTO;
import com.meetkey.server.domain.member.entity.mapping.MemberPhoto;
import com.meetkey.server.domain.member.service.ProfileService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.s3.S3Service;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.meetkey.server.domain.member.dto.ProfileReqDTO.*;
import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Tag(name = "Profile", description = "사용자 프로필 조회 및 수정 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class ProfileController {

    private final ProfileService profileService;
    private final S3Service s3Service;

    @Operation(summary = "프로필 정보 수정 API", description = "사용자의 활동 지역(문자열), 한줄 소개, 언어 정보를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = ProfileUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
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

    @Operation(summary = "위치 정보(좌표) 수정 API", description = "사용자의 위도(latitude), 경도(longitude)를 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
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

    @Operation(summary = "프로필 수정용 정보 조회 API", description = "프로필 수정 화면에 진입할 때 필요한 기존 정보(이름, 나이, 위치, 소개 등)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = ProfileUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
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

    @Operation(summary = "관심사 수정 API", description = "관심사 수정 시 관심사를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = InterestResponse.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
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
    @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = InterestCategoryResponse.class)))
    @GetMapping("/me/interest")
    public ResponseEntity<BasicResponse<InterestCategoryResponse>> getInterestCategory() {
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, profileService.getAllInterests()));
    }

    @Operation(summary = "성향 조회 API", description = "온본딩 및 성향 수정 시 성향들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = PersonalityCategoryResponse.class)))
    @GetMapping("/me/personality")
    public ResponseEntity<BasicResponse<PersonalityCategoryResponse>> getPersonalityCategory() {
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, profileService.getAllPersonality()));
    }

    @Operation(summary = "성향 수정 API", description = "성향 수정 시 성향을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = PersonalityUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
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

    @Operation(summary = "내 프로필 상세 조회 API", description = "마이페이지 등에서 내 프로필 전체 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "200", responseCode = "요청 성공", content = @Content(schema = @Schema(implementation = MyProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
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

    @Operation(summary = "상대방 프로필 조회 API", description = "타인의 프로필을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(description = "200", responseCode = "요청 성공", content = @Content(schema = @Schema(implementation = OtherProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/{targetId}")
    public ResponseEntity<BasicResponse<OtherProfileResponse>> getOtherProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "조회할 상대방 회원 ID", example = "1")
            @PathVariable Long targetId
    ) {
        Long myId = customUserDetails.getMemberId();

        OtherProfileResponse response = profileService.getOtherProfile(myId, targetId);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "추천/비추천 토글 API", description = "프로필에서 추천/비추천 토글기능입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "MEMBER4001: 해당 사용자를 찾을 수 없습니다."),
    })
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

    @Operation(summary = "업로드용 Presigned Url 발급 API", description = "프론트에서 파일 업로드 전에 요청")
    @PostMapping("/photos")
    public BasicResponse<List<ProfileResDTO.MemberPhotoUrl>> getMemberPhotoUploadUrl(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody List<ProfileReqDTO.PhotoInfo> photoInfos
            ){
        Long memberId = customUserDetails.getMemberId();

        List<ProfileResDTO.MemberPhotoUrl> responses = photoInfos.stream()
                .map(info -> s3Service.generateMemberPhotoPresignedUrl(memberId, info.fileName(), info.contentType()))
                .collect(Collectors.toList());

        return BasicResponse.success(CommonSuccessStatus._OK, responses);
    }

    @PostMapping("/photos/register")
    public BasicResponse<Void> registerMemberPhotos(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody List<String> s3Keys // 프론트가 업로드 성공 후 보낸 key 리스트
    ) {
        // 2. 새로운 s3Keys들을 MemberPhoto 엔티티로 만들어 저장
        Long memberId = customUserDetails.getMemberId();
        s3Service.registerMemberPhotoKeys(memberId, s3Keys);

        return BasicResponse.success(CommonSuccessStatus._OK, null);
    }

    @Operation(summary = "내 프로필 사진 조회 API", description = "로그인한 사용자의 모든 프로필 사진 URL 리스트를 가져옵니다.")
    @GetMapping("/photos")
    public BasicResponse<List<String>> getMyPhotos(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long memberId = customUserDetails.getMemberId();
        List<String> photoUrls = s3Service.getMemberPhotoUrls(memberId);

        return BasicResponse.success(CommonSuccessStatus._OK, photoUrls);
    }
}

