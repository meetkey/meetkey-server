package com.meetkey.server.domain.report.dto;

import com.meetkey.server.domain.report.enums.ReportType;
import lombok.Builder;

import java.util.List;

public class ReportReqDTO {
    @Builder
    public record CreateReport (
            ReportType reportType,
            String body,
            List<String> imageUrls
    ){}
}
