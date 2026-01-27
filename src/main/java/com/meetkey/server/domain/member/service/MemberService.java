package com.meetkey.server.domain.member.service;


import com.meetkey.server.domain.member.dto.MemberReqDTO;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.SocialLogin;
import com.meetkey.server.domain.member.enums.Provider;
import com.meetkey.server.domain.member.enums.Role;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.member.repository.SocialLoginRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final SocialLoginRepository socialLoginRepository;

    @Transactional
    public Member signup(Provider provider, String providerId, MemberReqDTO.Signup req) {
        Member member = Member.builder()
                .name(req.name())
                .targetLanguage(req.targetLanguage())
                .phoneNumber(req.phoneNumber())
                .gender(req.gender())
                .birthday(req.birthday())
                .firstLanguage(req.firstLanguage())
                .homeTown(req.homeTown())
                .targetLanguageLevel(req.targetLanguageLevel())
                .build();

        memberRepository.save(member);

        SocialLogin socialMember = SocialLogin.builder()
                .member(member)
                .provider(provider)
                .providerId(providerId)
                .build();
        socialLoginRepository.save(socialMember);

        return member;
    }

    @Transactional
    public Member devSignup(Provider provider, String providerId, MemberReqDTO.Signup req, String name) {
        Member member = Member.builder()
                .gender(req.gender())
                .name(name)
                .targetLanguage(req.targetLanguage())
                .phoneNumber(req.phoneNumber())
                .birthday(req.birthday())
                .firstLanguage(req.firstLanguage())
                .homeTown(req.homeTown())
                .targetLanguageLevel(req.targetLanguageLevel())
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();

        memberRepository.save(member);

        SocialLogin socialMember = SocialLogin.builder()
                .member(member)
                .provider(provider)
                .providerId(providerId)
                .build();
        socialLoginRepository.save(socialMember);

        return member;
    }

    @Transactional
    public void updateRefreshToken(String username, String newRefresh) {
        Long memberId = Long.parseLong(username);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        if (newRefresh != null) {
            member.changeRefreshToken(newRefresh, 86400000L);
        } else {
            member.changeRefreshToken(null,0L);
        }
    }

    public boolean isRefreshTokenExists(String refreshToken){
        return memberRepository.existsByRefreshToken(refreshToken);
    }
}
