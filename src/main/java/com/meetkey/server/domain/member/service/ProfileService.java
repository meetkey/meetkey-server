package com.meetkey.server.domain.member.service;

import com.meetkey.server.domain.member.converter.ProfileConverter;
import com.meetkey.server.domain.member.dto.ProfileReqDTO;
import com.meetkey.server.domain.member.dto.ProfileResDTO;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProfileConverter profileConverter;

    public ProfileUpdateResponse updateProfile(Long userId, ProfileReqDTO.ProfileUpdateRequest request) {
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        member.updateProfileInfo(request.location(), request.bio());

        return profileConverter.toProfileUpdateRes(member);
    }

}
