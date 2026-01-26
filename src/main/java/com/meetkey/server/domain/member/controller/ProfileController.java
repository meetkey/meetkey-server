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
    public ResponseEntity<BasicResponse<ProfileUpdateResponse>> updateProfile(
            @RequestAttribute("userId") Long userId,
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileUpdateResponse response = profileService.updateProfile(userId, request);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

}
