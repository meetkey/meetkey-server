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
            @RequestAttribute("userId") Long userId,
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileResponse response = profileService.updateProfile(userId, request);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

    @Operation(summary = "프로필 수정 시 조회 API", description = "이름, 나이, 위치, 한줄 소개를 조회합니다.")
    @GetMapping("/me/profile")
    public ResponseEntity<BasicResponse<ProfileResponse>> getProfile(
        @RequestAttribute("userId") Long userId
    ) {
        ProfileResponse response = profileService.getMyProfile(userId);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

}
