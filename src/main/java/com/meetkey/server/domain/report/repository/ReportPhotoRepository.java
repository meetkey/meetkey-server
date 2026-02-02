package com.meetkey.server.domain.report.repository;

import com.meetkey.server.domain.report.entity.mapping.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Long> {
}
