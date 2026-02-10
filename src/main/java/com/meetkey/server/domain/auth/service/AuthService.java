package com.meetkey.server.domain.auth.service;

import com.meetkey.server.domain.auth.entity.RefreshToken;
import com.meetkey.server.domain.auth.repository.RefreshTokenRepository;
import com.meetkey.server.domain.badge.entity.Badge;
import com.meetkey.server.domain.badge.enums.BadgeLevel;
import com.meetkey.server.domain.badge.service.BadgeService;
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

    @Value("${kakao.app-key}")
    private String kakaoAppKey;
    @Value(("{apple.app-key}"))
    private String appleAppKey;

    private final KakaoOauthClient kakaoOauthClient;
    private final AppleOauthClient appleClient;

    private final OauthOidcHelper oAuthOIDCHelper;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final BadgeService badgeService;

    private final SocialLoginRepository socialLoginRepository;
    private final RefreshTokenRepository refreshTokenRepository;

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

        // 회원 가입시 자동적으로 뱃지 생성
        Badge initalBadge = Badge.builder()
                .member(member)
                .total_score(0)
                .level(BadgeLevel.NONE)
                .build();

        // 밋키 서비스 토큰 발급
        return getJwtResponseDTO(member);
    }

    @Transactional
    public JwtResDTO.JwtResponse login(Provider provider, String idToken){
        String providerId;
        if (provider == Provider.KAKAO) {
            providerId = getKakaoProviderIdFromIdToken(idToken);
        } else if (provider == Provider.APPLE) {
            providerId = getAppleProviderIdFromIdToken(idToken);
        } else {
            throw new AuthException(AuthErrorStatus.INVALID_SOCIAL);
        }

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
        jwtUtil.isValid(refreshToken, false);

        if (refreshTokenRepository.findById(refreshToken).isEmpty()) {
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccess = jwtUtil.createJwt(username, role, true);
        String newRefresh = jwtUtil.createJwt(username, role, false);

        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.save(new RefreshToken(newRefresh, username));

        return JwtResDTO.JwtResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .memberId(Long.parseLong(username))
                .isNewMember(false)
                .build();
    }

    private JwtResDTO.JwtResponse getJwtResponseDTO(Member member) {
        String accessToken = jwtUtil.createJwt(member.getId().toString(), member.getRole().toString(), true);
        String refreshToken = jwtUtil.createJwt(member.getId().toString(), member.getRole().toString(), false);

        // redis에 refreshtoken 저장
        refreshTokenRepository.save(new RefreshToken(refreshToken, member.getId().toString()));

        return JwtResDTO.JwtResponse.builder()
                .memberId(member.getId())
                .isNewMember(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private JwtResDTO.JwtResponse getDevJwtResponseDTO(Member member) {
        String accessToken = jwtUtil.createDevJwt(member.getId().toString(), member.getRole().toString(), true);
        String refreshToken = jwtUtil.createDevJwt(member.getId().toString(), member.getRole().toString(), false);

        // redis에 refreshToken 저장
        refreshTokenRepository.saveDev(new RefreshToken(refreshToken, member.getId().toString()));

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
        OidcDTO.OIDCPublicKeys response = kakaoOauthClient.getKakaoOIDCOpenKeys();
        OidcDTO.OIDCDecodePayload payload = oAuthOIDCHelper.getPayloadFromIdToken(
                idToken,
                "https://kauth.kakao.com",
                kakaoAppKey,
                response
        );

        return payload.sub();
    }

}
