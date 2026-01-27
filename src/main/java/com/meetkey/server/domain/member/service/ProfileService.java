package com.meetkey.server.domain.member.service;

import com.meetkey.server.domain.member.converter.ProfileConverter;
import com.meetkey.server.domain.member.entity.Interest;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.Preference;
import com.meetkey.server.domain.member.entity.mapping.InterestMember;
import com.meetkey.server.domain.member.enums.InterestType;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.InterestMemberRepository;
import com.meetkey.server.domain.member.repository.InterestRepository;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.member.repository.PreferenceRepository;
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
    private final PreferenceRepository preferenceRepository;

    public ProfileUpdateResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = getMember(memberId);

        member.updateProfileInfo(request.location(), request.bio(), request.first(), request.target(), request.level());

        return profileConverter.toProfileUpdateResponse(member);
    }

    @Transactional(readOnly = true)
    public ProfileUpdateResponse getMyUpdateProfile(Long memberId) {
        Member member = getMember(memberId);

        return profileConverter.toProfileUpdateResponse(member);
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
    public InterestCategoryResponse getAllInterests() {
        return profileConverter.toCategoryResponse();
    }

    @Transactional(readOnly = true)
    public PersonalityCategoryResponse getAllPersonality() {
        return profileConverter.toPersonalityCategoryResponse();
    }

    public PersonalityUpdateResponse updatePersonality(Long memberId, PersonalityUpdateRequest request) {
        Member member = getMember(memberId);

        Preference preference = preferenceRepository.findById(member.getId()).orElse(null);
        if (preference == null) {
             preference = Preference.create(
                    member,
                    request.socialType(),
                    request.meetingType(),
                    request.chatType(),
                    request.friendType(),
                    request.relationType()
            );
            preferenceRepository.save(preference);
        } else {
            preference.update(
                    request.socialType(),
                    request.meetingType(),
                    request.chatType(),
                    request.friendType(),
                    request.relationType()
            );
        }
        return profileConverter.toPersonalityUpdateResponse(preference);
    }

    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(Long memberId) {
        Member member = getMember(memberId);

        Preference preference = preferenceRepository.findById(memberId).orElse(null);

        List<InterestMember> interestMembers = interestMemberRepository.findAllByMember(member);
        List<Interest> interests = interestMembers.stream()
                .map(InterestMember::getInterest)
                .toList();

        return profileConverter.toProfileResponse(member, interests, preference);
    }


    // 사용자 찾기 공통 로직
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }



}
