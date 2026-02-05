package com.meetkey.server.domain.member.dto;

import com.meetkey.server.domain.member.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class ProfileReqDTO {

    @Schema(description = "프로필 정보 수정 요청 DTO")
    public record ProfileUpdateRequest(
            @Schema(description = "현재 위치", example = "서울시 마포구")
            String location,

            @Schema(description = "위도 (업데이트 할 경우 필수)", example = "37.5665")
            Double latitude,

            @Schema(description = "경도 (업데이트 할 경우 필수)", example = "126.9780")
            Double longitude,

            @Schema(description = "한줄소 (Bio)", example = "안녕하세요! 편하게 연락주세요~")
            String bio,

            @Schema(description = "모국어", example = "KOREAN")
            Language first,

            @Schema(description = "학습 목표 언어 (Enum)", example = "ENGLISH")
            Language target,

            @Schema(description = "목표 언어 레벨 (Enum)", example = "BEGINNER")
            Level level
    ) {}

    @Schema(description = "관심사 수정 요청 DTO")
    public record InterestUpdateRequest(
            @Schema(description = "관심사 Enum 리스트", example = "[\"GAME\", \"TRAVEL\", \"MUSIC\"]")
            List<InterestType> interests
    ) {}


    @Builder
    @Schema(description = "성향 수정 요청 DTO")
    public record PersonalityUpdateRequest(

            @Schema(description = "성격 유형", example = "EXTROVERT")
            SocialType socialType,

            @Schema(description = "만남 선호 방식", example = "ONE")
            MeetingType meetingType,

            @Schema(description = "채팅 스타일", example = "BALANCED")
            ChatType chatType,

            @Schema(description = "친구 스타일(성별)", example = "SAME_GENDER")
            FriendType friendType,

            @Schema(description = "관계 유형", example = "CASUAL")
            RelationType relationType
    ) {}

    @Schema(description = "상대방 평가(추천/비추천) 요청 DTO (프로필 내에서)")
    public record EvaluationRequest(
            @Schema(description = "평가할 상대방의 Member ID", example = "10")
            Long targetMemberId,

            @Schema(description = "평가 유형 (RECOMMEND / NOT_RECOMMEND)", example = "RECOMMEND")
            EvaluationType type
    ) {}

    @Schema(description = "위치 정보 실시간 업데이트 요청 DTO")
    public record LocationUpdateRequest(

            @Schema(description = "위도", example = "37.5509")
            Double latitude,

            @Schema(description = "경도", example = "126.9410")
            Double longitude
    ) {}

    public record PhotoInfo(
            String fileName,
            String contentType
    ){}
}
