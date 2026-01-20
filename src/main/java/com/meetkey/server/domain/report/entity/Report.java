package com.meetkey.server.domain.report.entity;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.report.enums.ReportStatus;
import com.meetkey.server.domain.report.enums.ReportType;
import com.meetkey.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "report")
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "reporter_member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporterMember;

    @JoinColumn(name = "reported_member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member reportedMember;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportType reportType = ReportType.OTHER;

    @Column(nullable = false, length = 1000)
    private String body;
}
