package com.meetkey.server.domain.member.service;

import com.meetkey.server.domain.member.converter.ProfileConverter;
import com.meetkey.server.domain.member.entity.Interest;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.InterestMember;
import com.meetkey.server.domain.member.enums.InterestType;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.InterestMemberRepository;
import com.meetkey.server.domain.member.repository.InterestRepository;
import com.meetkey.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.meetkey.server.domain.member.dto.ProfileReqDTO.*;
import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProfileConverter profileConverter;
    private final InterestRepository interestRepository;
    private final InterestMemberRepository interestMemberRepository;

    public ProfileResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = getMember(memberId);

        member.updateProfileInfo(request.location(), request.bio());

        return profileConverter.toProfileRes(member);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long memberId) {
        Member member = getMember(memberId);

        return profileConverter.toProfileRes(member);
    }

    public InterestResponse updateInterests(Long memberId, List<InterestType> interestNames) {
        Member member = getMember(memberId);

        // 기존 관심사 삭제
        interestMemberRepository.deleteAllByMember(member);

        // 요청받은 관심사 조회
        List<Interest> interests = interestRepository.findAllByTypeIn(interestNames);

        List<InterestMember> newMappings = interests.stream()
                .map(interest -> InterestMember.create(member, interest))
                .collect(Collectors.toList());

        interestMemberRepository.saveAll(newMappings);

        return profileConverter.toInterestResponse(interests);
    }

    @Transactional(readOnly = true)
    public InterestResponse getInterests(Long memberId) {
        Member member = getMember(memberId);

        List<InterestMember> mappings = interestMemberRepository.findAllByMember(member);

        // InterestMember -> Interest 추출
        List<Interest> interests = mappings.stream()
                .map(InterestMember::getInterest)
                .collect(Collectors.toList());

        return profileConverter.toInterestResponse(interests);

    }

    // 사용자 찾기 공통 로직
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }

}
