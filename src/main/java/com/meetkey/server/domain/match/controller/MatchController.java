package com.meetkey.server.domain.match.controller;

import com.meetkey.server.domain.match.dto.MatchListResDTO;
import com.meetkey.server.domain.match.dto.RecommendationReqDTO;
import com.meetkey.server.domain.match.dto.SwipeReqDTO;
import com.meetkey.server.domain.match.dto.SwipeResDTO;
import com.meetkey.server.domain.match.service.MatchService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "사용자 추천 API", description = "사용자를 추천 받습니다. 일일 최대 10명 추천 (프리미엄 무제한).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "MATCH4041: 위치 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/recommendations")
    public BasicResponse<MatchListResDTO> getRecommendations(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "추천 필터 조건") @ModelAttribute @Valid RecommendationReqDTO request
    ) {
        return BasicResponse.success(CommonSuccessStatus._OK, matchService.getRecommendations(userDetails.getMemberId(), request));
    }

    @Operation(summary = "스와이프 결과 전송", description = "추천된 사용자의 관심 여부(LIKE/DISLIKE)를 전송합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "400", description = "MATCH4001: 본인 스와이프 불가, MATCH4002: 이미 스와이프함", content = @Content(schema = @Schema(implementation = BasicResponse.class))),
        @ApiResponse(responseCode = "404", description = "MATCH4042: 대상을 찾을 수 없음, MATCH4043: 사용자(본인) 찾을 수 없음", content = @Content(schema = @Schema(implementation = BasicResponse.class)))
    })
    @PostMapping("/swipe")
    public BasicResponse<SwipeResDTO> swipe(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody @Valid SwipeReqDTO request
    ) {
        return BasicResponse.success(CommonSuccessStatus._OK, matchService.swipe(userDetails.getMemberId(), request));
    }
}
