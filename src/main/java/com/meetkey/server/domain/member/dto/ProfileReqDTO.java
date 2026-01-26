package com.meetkey.server.domain.member.dto;

import com.meetkey.server.domain.member.enums.InterestType;

import java.util.List;

public class ProfileReqDTO {

    public record ProfileUpdateRequest(
            String location,
            String bio
    ) {}

    public record InterestUpdateRequest(
            List<InterestType> interests
    ) {}

}
