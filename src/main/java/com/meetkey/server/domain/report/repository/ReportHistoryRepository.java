package com.meetkey.server.domain.report.repository;

import com.meetkey.server.domain.report.entity.mapping.ReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {
}
