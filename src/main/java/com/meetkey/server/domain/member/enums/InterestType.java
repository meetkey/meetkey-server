package com.meetkey.server.domain.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterestType {
    // 일상 카테고리
    TRAVEL("여행", InterestCategory.DAILY),
    CAFE("카페 탐방", InterestCategory.DAILY),
    RESTAURANT("맛집 찾기", InterestCategory.DAILY),
    WALK("산책", InterestCategory.DAILY),
    PET("반려 동물", InterestCategory.DAILY),
    VLOG("일상 브이로그", InterestCategory.DAILY),
    KNIT("뜨개질", InterestCategory.DAILY),
    PHOTO("사진 찍기", InterestCategory.DAILY),
    LIFE("미니멀 라이프", InterestCategory.DAILY),
    DEVELOP("자기 계발", InterestCategory.DAILY),

    // 문화 카테고리
    MOVIE("영화", InterestCategory.CULTURE),
    DRAMA("드라마", InterestCategory.CULTURE),
    MUSIC("음악", InterestCategory.CULTURE),
    KPOP("K-POP", InterestCategory.CULTURE),
    POP("해외 팝송", InterestCategory.CULTURE),
    NETFLIX("넷플릭스", InterestCategory.CULTURE),
    YOUTUBE("유튜브", InterestCategory.CULTURE),
    WEBTOON("웹툰/만화", InterestCategory.CULTURE),
    ANIMATION("애니메이션", InterestCategory.CULTURE),
    GAME("게임", InterestCategory.CULTURE),
    BOOK("책", InterestCategory.CULTURE),

    // 지식 카테고리
    LANGUAGE("언어 공부", InterestCategory.KNOWLEDGE),
    STOCK("주식", InterestCategory.KNOWLEDGE),
    INVESTMENT("투자", InterestCategory.KNOWLEDGE),
    NEWS("뉴스", InterestCategory.KNOWLEDGE),
    SOCIALISSUES("사회 이슈", InterestCategory.KNOWLEDGE),
    TECH("테크/IT", InterestCategory.KNOWLEDGE),
    BUSINESS("비즈니스", InterestCategory.KNOWLEDGE),
    DESIGN("디자인",  InterestCategory.KNOWLEDGE),
    MARKETING("마케팅", InterestCategory.KNOWLEDGE),
    CAREER("커리어", InterestCategory.KNOWLEDGE),
    JOB("취업", InterestCategory.KNOWLEDGE),
    ;
    private final String name;
    private final InterestCategory category;
}
