package com.meetkey.server.domain.member.entity.mapping;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestMemberId {
    @Column(name = "interest_id")
    private Long interestId;

    @Column(name = "member_id")
    private Long memberId;


}
