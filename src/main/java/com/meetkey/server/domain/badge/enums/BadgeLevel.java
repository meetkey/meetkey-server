package com.meetkey.server.domain.badge.enums;

import com.meetkey.server.domain.badge.entity.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeLevel {
    NONE("없음", 0),
    BRONZE("브론즈", 70),
    SILVER("실버", 80),
    GOLD("골드", 90);

    private final String name;
    private final int minScore;

    public static BadgeLevel fromScore(int score) {
        if (score >= GOLD.minScore) return GOLD;
        if (score >= SILVER.minScore) return SILVER;
        if (score >= BRONZE.minScore) return BRONZE;
        return NONE;
    }
}
