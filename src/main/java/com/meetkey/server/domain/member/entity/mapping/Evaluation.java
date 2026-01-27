package com.meetkey.server.domain.member.entity.mapping;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.enums.EvaluationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "evalutaion",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"from_member_id", "to_member_id"})
        })
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    private Member toMember;

    @Enumerated(EnumType.STRING)
    private EvaluationType type;

    @Builder
    public Evaluation(Member fromMember, Member toMember, EvaluationType type) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.type = type;
    }

    public void updateType(EvaluationType type) {
        this.type = type;
    }



}
