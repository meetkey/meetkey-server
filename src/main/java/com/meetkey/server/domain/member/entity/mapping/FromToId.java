package com.meetkey.server.domain.member.entity.mapping;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class FromToId {
    @Column(name = "from_id")
    private Long fromId;
    @Column(name = "to_id")
    private Long toId;
}
