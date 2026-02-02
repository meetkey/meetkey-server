package com.meetkey.server.domain.report.entity.mapping;

import com.meetkey.server.domain.report.entity.Report;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "report_photo")
public class ReportPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "report_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Report report;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reportPhotoUrl;
}
