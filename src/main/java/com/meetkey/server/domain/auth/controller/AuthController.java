package com.meetkey.server.domain.auth.controller;

import com.meetkey.server.domain.auth.service.AuthService;
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
    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Value("${admin.secret}")
    private String adminSecret;

    @GetMapping("/test")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal CustomUserDetails details){
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, memberRepository.findById(1L)));
    }

    @PostMapping("/test")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> test(
            @RequestHeader(value = "X-Admin-Secret", required = false) String secret
    ) {
        if (adminSecret == null || !adminSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(BasicResponse.error(CommonErrorStatus._FORBIDDEN, null));
        }

        Member m = Member.builder().role(Role.valueOf("ROLE_ADMIN")).build();
        Member savedMember = memberRepository.save(m);

        String accessToken = jwtUtil.createJwt("access", String.valueOf(savedMember.getId()), "ROLE_ADMIN", 1000 * 60 * 60 * 24 * 365L);
        String refreshToken = jwtUtil.createJwt("refresh",  String.valueOf(savedMember.getId()), "ROLE_ADMIN", 1000 * 60 * 60 * 24 * 3650L);

        memberService.updateRefreshToken(String.valueOf(savedMember.getId()), refreshToken);

        JwtResDTO.JwtResponse jwts = JwtResDTO.JwtResponse.builder()
                .isNewMember(true)
                .accessToken(accessToken)
                .memberId(savedMember.getId())
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @PostMapping("/login")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> login(
            @RequestParam("provider") Provider provider,
            @RequestBody OauthReqDTO.LoginReq loginReq
    ) {
        JwtResDTO.JwtResponse jwts = authService.login(provider, loginReq.idToken(), loginReq.nonce());
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @PostMapping("/signup")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> signup(
            @RequestParam("provider") Provider provider,
            @RequestBody OauthReqDTO.SignupReq signupReq
    ){
        if (provider.equals(Provider.KAKAO)) {
            JwtResDTO.JwtResponse jwts = authService.signup(provider, signupReq);
            return ResponseEntity.ok()
                    .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BasicResponse.error(CommonErrorStatus._BAD_REQUEST, null));
        }
    }


    @PostMapping("/reissue")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> reissue(
            @RequestHeader(value = "refresh", required = false) String refreshToken
    ) {
        JwtResDTO.JwtResponse jwts = authService.reissue(refreshToken);
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

}
