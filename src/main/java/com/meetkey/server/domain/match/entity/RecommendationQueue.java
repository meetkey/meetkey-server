package com.meetkey.server.domain.match.entity;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "recommendation_queue")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendationQueue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne
    private Member member;

    @JoinColumn(name = "target_member_id", nullable = false)
    @ManyToOne
    private Member targetMember;

    @Column(nullable = false)
    private Double score;

    @Builder.Default
    private Boolean isSwiped = false;

    @Builder.Default
    private Boolean isRecycled = false;

    public void markSwiped() {
        this.isSwiped = true;
    }

    public void recycle() {
        this.isSwiped = false;
        this.isRecycled = true;
    }
}
