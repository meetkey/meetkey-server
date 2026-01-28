package com.meetkey.server.domain.auth.controller;

import com.meetkey.server.domain.auth.service.AuthService;
import com.meetkey.server.domain.auth.service.SmsService;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.enums.Provider;
import com.meetkey.server.domain.member.enums.Role;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.member.service.MemberService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonErrorStatus;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import com.meetkey.server.global.security.jwt.JwtUtil;
import com.meetkey.server.global.security.jwt.dto.JwtResDTO;
import com.meetkey.server.global.security.oauth.dto.OauthReqDTO;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final SmsService smsService;


    @Value("${admin.secret}")
    private String adminSecret;

    @Operation(summary = "마스터 계정 발급")
    @PostMapping("/test")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> test(
            @RequestHeader(value = "X-Admin-Secret", required = false) String secret,
            @RequestBody OauthReqDTO.SignupReq signupReq
    ) {
        if (adminSecret == null || !adminSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(BasicResponse.error(CommonErrorStatus._FORBIDDEN, null));
        }

        JwtResDTO.JwtResponse jwts = authService.devSignup(signupReq);
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @Operation(summary = "로그인 API", description = "소셜 정보를 통해 로그인을 합니다. (애플 로그인의 경우 아직 작동 X)")
    @PostMapping("/login")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> login(
            @RequestParam("provider") Provider provider,
            @RequestBody OauthReqDTO.LoginReq loginReq
    ) {
        JwtResDTO.JwtResponse jwts = authService.login(provider, loginReq.idToken());
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @Operation(summary = "회원가입 API", description = "소셜 로그인에 회원 정보가 없으면 회원가입을 합니다. (애플 로그인의 경우 아직 작동 X)")
    @PostMapping("/signup")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> signup(
            @RequestParam("provider") Provider provider,
            @RequestBody OauthReqDTO.SignupReq signupReq
    ) {
        JwtResDTO.JwtResponse jwts = authService.signup(provider, signupReq);
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @Operation(summary = "토큰 재발급 API", description = "access 토큰 기간 만료 시 refresh 토큰으로 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> reissue(
            @RequestHeader(value = "refresh", required = false) String refreshToken
    ) {
        JwtResDTO.JwtResponse jwts = authService.reissue(refreshToken);
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @Operation(summary = "인증번호 발송 API", description = "phone 해당하는 번호에 인증번호를 발송합니다.")
    @PostMapping("/sms/send")
    public ResponseEntity<BasicResponse<Boolean>> sendAuthCode(@RequestParam String phone) {
        smsService.sendAuthCode(phone);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, true));
    }

    @Operation(summary = "인증번호 검증 API", description = "인증번호가 일치하는지 검증합니다. (인증시간 180초)")
    @PostMapping("/sms/verify")
    public ResponseEntity<BasicResponse<Boolean>> verifyAuthCode(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String phone,
            @RequestParam String code
    ) {

        Long memberId = customUserDetails.getMemberId();
        smsService.completeAuthentication(memberId, phone, code);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, true));
    }

}
