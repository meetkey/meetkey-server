package com.meetkey.server.domain.member.service;


import com.meetkey.server.domain.member.dto.MemberReqDTO;
import com.meetkey.server.domain.member.dto.MemberResDTO;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.SocialLogin;
import com.meetkey.server.domain.member.entity.mapping.FcmToken;
import com.meetkey.server.domain.member.entity.mapping.FromToId;
import com.meetkey.server.domain.member.entity.mapping.MemberBlock;
import com.meetkey.server.domain.member.enums.Provider;
import com.meetkey.server.domain.member.enums.Role;
import com.meetkey.server.domain.member.enums.Status;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberBlockRepository;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.member.repository.SocialLoginRepository;
import com.meetkey.server.domain.notification.repository.FcmTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberBlockRepository memberBlockRepository;

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
                .role(Role.ROLE_ADMIN)
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
    public MemberResDTO.Block blockMember(Long fromId, Long toId){
        // 멤버 있는지 없는지 확인
        Member fromMember = findMemberById(fromId);
        Member toMember = findMemberById(toId);
        FromToId blockId = new FromToId(fromId, toId);

        // 중복 차단인지 확인
        if (memberBlockRepository.existsById(blockId)) {
            throw new MemberException(MemberErrorStatus.ALREADY_BLOCKED);
        }

        MemberBlock memberBlock = MemberBlock.builder()
                .memberBlockId(blockId)
                .fromMember(fromMember)
                .toMember(toMember)
                .build();

        memberBlockRepository.save(memberBlock);

        return MemberResDTO.Block.builder()
                .fromMemberId(fromId)
                .toMemberId(toId)
                .build();
    }

    @Transactional
    public void updateMembershipStatus(Long memberId){
        Member member = findMemberById(memberId);

        member.updateMemberShip();
    }

    // FCM 토큰 저장
    @Transactional
    public void saveFcmToken(Long memberId, String token) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        boolean isExist = fcmTokenRepository.existsByMemberAndToken(member, token);
        if (!isExist) {
            fcmTokenRepository.save(FcmToken.builder()
                    .member(member)
                    .token(token)
                    .build());
        }
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }
}
