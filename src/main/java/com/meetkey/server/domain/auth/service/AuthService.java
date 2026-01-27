package com.meetkey.server.domain.auth.service;

import com.meetkey.server.domain.member.dto.MemberReqDTO;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.SocialLogin;
import com.meetkey.server.domain.member.enums.Provider;
import com.meetkey.server.domain.member.repository.SocialLoginRepository;
import com.meetkey.server.domain.member.service.MemberService;
import com.meetkey.server.global.security.jwt.dto.JwtResDTO;
import com.meetkey.server.global.security.jwt.JwtUtil;
import com.meetkey.server.global.security.oauth.OauthOidcHelper;
import com.meetkey.server.global.security.oauth.apple.AppleOauthClient;
import com.meetkey.server.global.security.oauth.converter.OauthConverter;
import com.meetkey.server.global.security.oauth.dto.OauthReqDTO;
import com.meetkey.server.domain.auth.exception.AuthErrorStatus;
import com.meetkey.server.domain.auth.exception.AuthException;
import com.meetkey.server.global.security.oauth.dto.OidcDTO;
import com.meetkey.server.global.security.oauth.kakao.KakaoOauthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Long ACCESS_TOKEN_EXP = 600000L; // 10분
    private static final Long REFRESH_TOKEN_EXP = 86400000L; // 24시간

    @Value("${kakao.app-key}")
    private String kakaoAppKey;
    @Value(("{apple.app-key}"))
    private String appleAppKey;

    private final KakaoOauthClient kakaoClient;
    private final AppleOauthClient appleClient;

    private final OauthOidcHelper oAuthOIDCHelper;
    private final SocialLoginRepository socialLoginRepository;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @Transactional
    public JwtResDTO.JwtResponse devSignup(OauthReqDTO.SignupReq req){
        String providerId = UUID.randomUUID().toString();
        Provider provider = Provider.KAKAO;
        String name = "dev_" + UUID.randomUUID().toString();

        MemberReqDTO.Signup memberReqDTO = OauthConverter.toMemberSignUpDTO(req);
        Member member = memberService.devSignup(provider, providerId, memberReqDTO, name);

        return getDevJwtResponseDTO(member);
    }

    @Transactional
    public JwtResDTO.JwtResponse signup(Provider provider, OauthReqDTO.SignupReq req){
        // 카카오 플랫폼 인증 및 ID 추출

        String providerId;
        if (provider == Provider.KAKAO) {
            providerId = getKakaoProviderIdFromIdToken(req.idToken());
        }
        else if (provider == Provider.APPLE) {
            providerId = getAppleProviderIdFromIdToken(req.idToken());
        }
        else throw new AuthException(AuthErrorStatus.INVALID_SOCIAL);


        MemberReqDTO.Signup memberReqDTO = OauthConverter.toMemberSignUpDTO(req);
        // Member 생성
        Member member = memberService.signup(provider, providerId, memberReqDTO);

        // 밋키 서비스 토큰 발급
        return getJwtResponseDTO(member);
    }

    @Transactional
    public JwtResDTO.JwtResponse login(Provider provider, String idToken){
        String providerId = getKakaoProviderIdFromIdToken(idToken);

        Optional<SocialLogin> socialMember =
                socialLoginRepository.findByProviderAndProviderId(provider, providerId);

        if (socialMember.isEmpty()){
            return JwtResDTO.JwtResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .isNewMember(true)
                    .memberId(null)
                    .build();
        }

        // 존재하면 jwtToken 발급
        Member member = socialMember.get().getMember();
        return getJwtResponseDTO(member);
    }

    @Transactional
    public JwtResDTO.JwtResponse reissue(String refreshToken){
        jwtUtil.validateRefreshToken(refreshToken);

        if (!memberService.isRefreshTokenExists(refreshToken)) {
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);


        String newAccess = jwtUtil.createJwt(
                "access", username, role, ACCESS_TOKEN_EXP);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, REFRESH_TOKEN_EXP);

        memberService.updateRefreshToken(username, newRefresh);

        return JwtResDTO.JwtResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .memberId(Long.parseLong(username))
                .isNewMember(false)
                .build();
    }

    private JwtResDTO.JwtResponse getJwtResponseDTO(Member member) {
        String accessToken = jwtUtil.createJwt("access", member.getId().toString(), member.getRole().toString(), ACCESS_TOKEN_EXP);
        String refreshToken = jwtUtil.createJwt("refresh", member.getId().toString(), member.getRole().toString(), REFRESH_TOKEN_EXP);

        member.changeRefreshToken(refreshToken, REFRESH_TOKEN_EXP);

        return JwtResDTO.JwtResponse.builder()
                .memberId(member.getId())
                .isNewMember(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private JwtResDTO.JwtResponse getDevJwtResponseDTO(Member member) {
        String accessToken = jwtUtil.createJwt("access", member.getId().toString(), member.getRole().toString(), 1000 * 60 * 60 * 24 * 365L);
        String refreshToken = jwtUtil.createJwt("refresh", member.getId().toString(), member.getRole().toString(), 1000 * 60 * 60 * 24 * 3650L);

        member.changeRefreshToken(refreshToken, REFRESH_TOKEN_EXP);

        return JwtResDTO.JwtResponse.builder()
                .memberId(member.getId())
                .isNewMember(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    private String getAppleProviderIdFromIdToken(String idToken){
        OidcDTO.OIDCPublicKeys response = appleClient.getAppleOIDCOpenKeys();
        OidcDTO.OIDCDecodePayload payload = oAuthOIDCHelper.getPayloadFromIdToken(
                idToken,
                "https://appleid.apple.com",
                appleAppKey,
                response
        );

        return payload.sub();
    }

    private String getKakaoProviderIdFromIdToken(String idToken){
        OidcDTO.OIDCPublicKeys response = kakaoClient.getKakaoOIDCOpenKeys();
        OidcDTO.OIDCDecodePayload payload = oAuthOIDCHelper.getPayloadFromIdToken(
                idToken,
                "https://kauth.kakao.com",
                kakaoAppKey,
                response
        );

        return payload.sub();
    }

}
