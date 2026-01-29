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

    @GetMapping("/recommendations")
    public BasicResponse<MatchListResDTO> getRecommendations(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute RecommendationReqDTO request
    ) {
        Member member = memberRepository.findById(userDetails.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));

        return BasicResponse.success(CommonSuccessStatus._OK, matchService.getRecommendations(member, request));
    }

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
