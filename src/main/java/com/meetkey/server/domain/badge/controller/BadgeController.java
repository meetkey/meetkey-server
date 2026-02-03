package com.meetkey.server.domain.badge.controller;

import com.meetkey.server.domain.badge.service.BadgeService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.meetkey.server.domain.badge.dto.BadgeResDTO.*;

@Tag(name = "Badge", description = "뱃지 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/badges")
public class BadgeController {

    private final BadgeService badgeService;

    @Operation(summary = "점수 획득 내역 조회 API", description = "내 뱃지 등급, 점수, 점수 획득 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = BadgeResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/me")
    public ResponseEntity<BasicResponse<BadgeResponse>> getBadgeDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long memberId = customUserDetails.getMemberId();

        BadgeResponse response = badgeService.getBadgeDetail(memberId);

        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, response));
    }

}
