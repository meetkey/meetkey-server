package com.meetkey.server.domain.member.service;

import com.meetkey.server.domain.member.converter.ProfileConverter;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.meetkey.server.domain.member.dto.ProfileReqDTO.*;
import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProfileConverter profileConverter;

    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        Member member = getMember(userId);

        member.updateProfileInfo(request.location(), request.bio());

        return profileConverter.toProfileRes(member);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long userId) {
        Member member = getMember(userId);

        return profileConverter.toProfileRes(member);
    }


    // 사용자 찾기 공통 로직
    private Member getMember(Long userId) {
        return memberRepository.findById(userId).orElseThrow(
                () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));
    }

}
