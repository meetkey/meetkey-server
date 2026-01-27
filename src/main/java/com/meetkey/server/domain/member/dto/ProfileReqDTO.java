package com.meetkey.server.domain.member.dto;

import com.meetkey.server.domain.member.enums.*;
import lombok.Builder;

import java.util.List;

public class ProfileReqDTO {

    public record ProfileUpdateRequest(
            String location,
            Double latitude,
            Double longitude,
            String bio,
            Language first,
            Language target,
            Level level
    ) {}

    public record InterestUpdateRequest(
            List<InterestType> interests
    ) {}

    @Builder
    public record PersonalityUpdateRequest(
            SocialType socialType,
            MeetingType meetingType,
            ChatType chatType,
            FriendType friendType,
            RelationType relationType
    ) {}

    public record EvaluationRequest(
            Long targetMemberId,
            EvaluationType type
    ) {}
}
