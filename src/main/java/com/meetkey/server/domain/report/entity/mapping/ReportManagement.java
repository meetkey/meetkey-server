package com.meetkey.server.domain.report.entity.mapping;

import com.meetkey.server.domain.report.entity.Report;
import com.meetkey.server.domain.report.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "report_management")
public class ReportManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "report_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Report report;

    @Column(nullable = false)
    private String managerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;



}
