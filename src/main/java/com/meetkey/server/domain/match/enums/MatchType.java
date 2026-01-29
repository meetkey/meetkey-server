package com.meetkey.server.domain.match.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchType {
    DAILY_MATCH,
    RECYCLE,
    RANDOM;
}
