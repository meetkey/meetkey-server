package com.meetkey.server.domain.member.entity.mapping;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "member_block")
public class MemberBlock extends BaseEntity {
    @EmbeddedId
    private FromToId memberBlockId;

    @MapsId("from_id")
    @JoinColumn(name = "from_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member fromMember;

    @MapsId("to_id")
    @JoinColumn(name = "to_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member toMember;
}
