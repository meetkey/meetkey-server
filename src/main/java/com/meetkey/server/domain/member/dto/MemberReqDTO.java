package com.meetkey.server.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetkey.server.domain.member.enums.*;
import com.meetkey.server.global.annotation.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class MemberReqDTO {
    @Builder
    public record Signup(
            @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
            @NotNull @NotBlank String name,
            @NotNull Gender gender,
            @NotNull HomeTown homeTown,
            @NotNull Language firstLanguage,
            @NotNull Language targetLanguage,
            @NotNull Level targetLanguageLevel,
            @NotNull @PhoneNumber String phoneNumber
    ){}

    public record FcmTokenReq(
            @Schema(description = "FCM 토큰", example = "fcm_token_example_123456")
            String token
    ) {}
}