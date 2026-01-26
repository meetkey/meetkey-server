package com.meetkey.server.domain.member.converter;

import com.meetkey.server.domain.member.entity.Interest;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.enums.InterestType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public InterestResponse toInterestResponse (List<Interest> interests) {
        List<InterestType> types = interests.stream()
                .map(Interest::getType)
                .collect(Collectors.toList());

        return InterestResponse.builder()
                .interests(types)
                .build();
    }
}
