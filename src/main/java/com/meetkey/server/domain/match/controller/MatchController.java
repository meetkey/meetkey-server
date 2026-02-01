package com.meetkey.server.domain.match.controller;

import com.meetkey.server.domain.match.dto.MatchListResDTO;
import com.meetkey.server.domain.match.dto.RecommendationReqDTO;
import com.meetkey.server.domain.match.dto.SwipeReqDTO;
import com.meetkey.server.domain.match.dto.SwipeResDTO;
import com.meetkey.server.domain.match.service.MatchService;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final MemberRepository memberRepository;

    @Operation(summary = "사용자 추천 API", description = "사용자를 추천 받습니다.")
    @GetMapping("/recommendations")
    public BasicResponse<MatchListResDTO> getRecommendations(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute @Valid RecommendationReqDTO request
    ) {
        Member member = memberRepository.findById(userDetails.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));

        return BasicResponse.success(CommonSuccessStatus._OK, matchService.getRecommendations(member, request));
    }

    @Operation(summary = "스와이프 결과 전송", description = "추천된 사용자의 관심 여부를 전송합니다.")
    @PostMapping("/swipe")
    public BasicResponse<SwipeResDTO> swipe(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody @Valid SwipeReqDTO request
    ) {
        Member member = memberRepository.findById(userDetails.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));

        return BasicResponse.success(CommonSuccessStatus._OK, matchService.swipe(member, request));
    }
}
