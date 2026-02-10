package com.meetkey.server.domain.badge.entity;

import com.meetkey.server.domain.badge.enums.BadgeLevel;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "badge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Badge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder.Default
    private int total_score = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BadgeLevel level;

    public void addScore(int score) {
        this.total_score += score;
    }


}
