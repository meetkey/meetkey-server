package com.meetkey.server.domain.member.entity.mapping;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "member_block")
public class MemberBlock extends BaseEntity {
    @EmbeddedId
    private FromToId memberBlockId;

    @MapsId("fromId")
    @JoinColumn(name = "from_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member fromMember;

    @MapsId("toId")
    @JoinColumn(name = "to_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member toMember;
}
