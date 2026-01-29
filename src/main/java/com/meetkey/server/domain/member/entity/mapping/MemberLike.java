package com.meetkey.server.domain.member.entity.mapping;

import com.meetkey.server.domain.match.enums.Action;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "member_like")
public class MemberLike extends BaseEntity {
    @EmbeddedId
    private FromToId memberLikeId;

    @MapsId("fromId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    private Member fromMember;

    @MapsId("toId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    private Member toMember;

    @Builder.Default
    private Boolean isMatched = false;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Builder.Default
    private Boolean isChatStarted = false;
}
