package com.meetkey.server.domain.member.dto;

public class ProfileReqDTO {

    public record ProfileUpdateRequest(
            String location,
            String bio
    ) {}

}
