package com.meetkey.server.domain.member.converter;

import com.meetkey.server.domain.member.dto.ProfileResDTO;
import com.meetkey.server.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;

import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Component
public class ProfileConverter {

    public ProfileUpdateResponse toProfileUpdateRes(Member member) {
        return ProfileUpdateResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .location(member.getLocation())
                .bio(member.getBio())
                .build();
    }
}
