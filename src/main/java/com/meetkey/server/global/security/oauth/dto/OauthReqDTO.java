package com.meetkey.server.global.security.oauth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetkey.server.domain.member.enums.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class OauthReqDTO {
    public record LoginReq(
            @NotNull String idToken,
            @NotNull String nonce
    ){}

    public record SignupReq(
            @NotNull String idToken,
            @NotNull String nonce,

            @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
            @NotNull Gender gender,
            @NotNull HomeTown homeTown,
            @NotNull Language firstLanguage,
            @NotNull Language targetLanguage,
            @NotNull Level targetLanguageLevel,

            @NotNull String phoneNumber,

            @NotNull List<InterestType> interests,
            @NotNull SocialType socialType,
            @NotNull MeetingType meetingType,
            @NotNull ChatType chatType,
            @NotNull FriendType friendType,
            @NotNull RelationType relationType
    ){}
}
