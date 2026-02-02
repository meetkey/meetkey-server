package com.meetkey.server.global.security.oauth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetkey.server.domain.member.enums.*;
import com.meetkey.server.global.annotation.PhoneNumber;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class OauthReqDTO {
    public record LoginReq(
            @NotNull String idToken
    ){}

    public record SignupReq(
            @NotNull String idToken,

            @NotNull String name,
            @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
            @NotNull Gender gender,
            @NotNull HomeTown homeTown,
            @NotNull Language firstLanguage,
            @NotNull Language targetLanguage,
            @NotNull Level targetLanguageLevel,

            @NotNull @PhoneNumber String phoneNumber
    ){}
}
