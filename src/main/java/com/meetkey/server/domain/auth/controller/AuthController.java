package com.meetkey.server.domain.auth.controller;

import com.meetkey.server.domain.auth.service.AuthService;
import com.meetkey.server.domain.auth.service.SmsService;
import com.meetkey.server.domain.member.enums.Provider;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonErrorStatus;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import com.meetkey.server.global.security.jwt.dto.JwtResDTO;
import com.meetkey.server.global.security.oauth.dto.OauthReqDTO;

import com.meetkey.server.global.security.oauth.dto.OidcDTO;
import com.meetkey.server.global.security.oauth.kakao.KakaoOauthClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final SmsService smsService;
    private final KakaoOauthClient kakaoOauthClient;


    @Value("${admin.secret}")
    private String adminSecret;

    @Operation(summary = "마스터 계정 발급", description = "노션에 마스터 토큰을 올려두었습니다. 이상 있는 경우 따밥/김채원 으로 연락주세요")
    @PostMapping("/test")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> test(
            @RequestHeader(value = "X-Admin-Secret", required = false) String secret,
            @Valid @RequestBody OauthReqDTO.SignupReq signupReq
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = JwtResDTO.JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "AUTH4003: 소셜 로그인 Provider 잘못 씀, AUTH4001: IdToken 파싱 과정 중 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> login(
            @RequestParam("provider") Provider provider,
            @Valid @RequestBody OauthReqDTO.LoginReq loginReq
    ) {
        JwtResDTO.JwtResponse jwts = authService.login(provider, loginReq.idToken());
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @Operation(summary = "회원가입 API", description = "전화번호는 국제번호 규격에 맞추어야 합니다. ex: +821012345678")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = JwtResDTO.JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "AUTH4003: 소셜 로그인 Provider 잘못 씀"),
    })
    @PostMapping("/signup")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> signup(
            @RequestParam("provider") Provider provider,
            @Valid @RequestBody OauthReqDTO.SignupReq signupReq
    ) {
        JwtResDTO.JwtResponse jwts = authService.signup(provider, signupReq);
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @Operation(summary = "토큰 재발급 API", description = "access 토큰 기간 만료 시 refresh 토큰으로 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = JwtResDTO.JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "AUTH4011: 리프레시 토큰이 잘못됨")
    })
    @PostMapping("/reissue")
    public ResponseEntity<BasicResponse<JwtResDTO.JwtResponse>> reissue(
            @RequestHeader(value = "refresh", required = false) String refreshToken
    ) {
        JwtResDTO.JwtResponse jwts = authService.reissue(refreshToken);
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, jwts));
    }

    @Operation(summary = "인증번호 발송 API", description = "phone 해당하는 번호에 인증번호를 발송합니다., 전화번호는 '-' 없이 숫자만 입력해주세요. (ex: 01012345678)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",  content =  @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "500", description = "AUTH5001: 인증번호 발송 실패(외부 API 오류)")
    })
    @PostMapping("/sms/send")
    public ResponseEntity<BasicResponse<Boolean>> sendAuthCode(
            @Parameter(description = "인증번호 받을 전화번호", example = "01012345678")
            @RequestParam("phone") String phone) {
        smsService.sendAuthCode(phone);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, true));
    }

    @Operation(summary = "인증번호 검증 API", description = "인증번호가 일치하는지 검증합니다. (인증시간 180초)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",  content =  @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "AUTH4002: 인증번호 불일치, MEMBER4041: 사용자를 찾을 수 없음"),
    })
    @PostMapping("/sms/verify")
    public ResponseEntity<BasicResponse<Boolean>> verifyAuthCode(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "인증번호 받을 전화번호", example = "01012345678")
            @RequestParam("phone") String phone,
            @Parameter(description = "수신받은 인증번호", example = "123456")
            @RequestParam("code") String code
    ) {

        Long memberId = customUserDetails.getMemberId();
        smsService.completeAuthentication(memberId, phone, code);

        return ResponseEntity
                .ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, true));
    }

    // OIDC Cache 설정 확인 테스트
    @GetMapping("/test/kakao-keys")
    public ResponseEntity<?> getKeys() {
        OidcDTO.OIDCPublicKeys keys = kakaoOauthClient.getKakaoOIDCOpenKeys();

        return ResponseEntity.ok(keys);
    }
}
