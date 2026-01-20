package com.meetkey.server.domain.member.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InterestCategory {
    DAILY("일상, 라이프스타일"),
    CULTURE("문화, 콘텐츠"),
    KNOWLEDGE("지식, 시사"),
    ;

    private final String description;
}