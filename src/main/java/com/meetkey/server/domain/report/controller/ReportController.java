package com.meetkey.server.domain.report.controller;

import com.meetkey.server.domain.report.dto.ReportReqDTO;
import com.meetkey.server.domain.report.service.ReportService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/{targetId}")
    public ResponseEntity<BasicResponse<Void>> createReport(
            @AuthenticationPrincipal CustomUserDetails reporter,
            @PathVariable Long targetId,
            @RequestBody ReportReqDTO.CreateReport req
    ) {
        reportService.createReport(reporter.getMemberId(), targetId, req);
        return ResponseEntity.ok()
                .body(BasicResponse.success(CommonSuccessStatus._OK, null));
    }
}
