package com.meetkey.server.domain.report.entity.mapping;

import com.meetkey.server.domain.report.entity.Report;
import com.meetkey.server.domain.report.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "report_history")
public class ReportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "report_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Report report;

    private String managerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private LocalDateTime suspendedAt;
    private LocalDateTime suspendedUntil;

    @Builder.Default
    private Boolean isActive = false;
}
