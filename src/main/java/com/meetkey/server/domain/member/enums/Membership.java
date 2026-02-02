package com.meetkey.server.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Membership {
    FREE("무료 회원"),
    PREMIUM("유료 회원");

    private final String description;
}
