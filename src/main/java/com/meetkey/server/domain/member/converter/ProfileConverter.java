package com.meetkey.server.domain.member.converter;

import com.meetkey.server.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import static com.meetkey.server.domain.member.dto.ProfileResDTO.*;

@Component
public class ProfileConverter {

    public ProfileResponse toProfileRes (Member member) {
        return ProfileResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .age(member.getAge())
                .location(member.getLocation())
                .bio(member.getBio())
                .build();
    }
}
