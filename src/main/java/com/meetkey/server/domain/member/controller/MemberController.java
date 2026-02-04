package com.meetkey.server.domain.member.controller;

import com.meetkey.server.domain.member.dto.MemberReqDTO;
import com.meetkey.server.domain.member.dto.ProfileResDTO;
import com.meetkey.server.domain.member.service.MemberService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.meetkey.server.domain.member.dto.MemberReqDTO.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "FCM 토큰 저장 API", description = "사용자의 FCM 토큰을 저장합니다. (앱이 켜지거나 로그인시에 호출)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = String.class ))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다.")
    })
    @PostMapping("/fcm-token")
    public ResponseEntity<BasicResponse<String>> saveFcmToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FcmTokenReq request
    ) {
        memberService.saveFcmToken(userDetails.getMemberId(), request.token());
        return ResponseEntity.ok(BasicResponse.success(CommonSuccessStatus._OK, "토큰 저장 완료"));
    }
}
