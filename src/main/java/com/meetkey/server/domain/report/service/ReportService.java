package com.meetkey.server.domain.report.service;

import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberRepository;
import com.meetkey.server.domain.report.dto.ReportReqDTO;
import com.meetkey.server.domain.report.entity.Report;
import com.meetkey.server.domain.report.entity.mapping.ReportHistory;
import com.meetkey.server.domain.report.entity.mapping.ReportPhoto;
import com.meetkey.server.domain.report.repository.ReportHistoryRepository;
import com.meetkey.server.domain.report.repository.ReportPhotoRepository;
import com.meetkey.server.domain.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReportService {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final ReportPhotoRepository reportPhotoRepository;
    private final ReportHistoryRepository reportHistoryRepository;

    public ReportService(MemberRepository memberRepository, ReportRepository reportRepository, ReportPhotoRepository reportPhotoRepository, ReportHistoryRepository reportHistoryRepository) {
        this.memberRepository = memberRepository;
        this.reportRepository = reportRepository;
        this.reportPhotoRepository = reportPhotoRepository;
        this.reportHistoryRepository = reportHistoryRepository;
    }

    @Transactional
    public void createReport(Long reporterId, Long targetId, ReportReqDTO.CreateReport req){
        Member reporterMember = memberRepository.findById(reporterId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        // report에 저장
        Report report = Report.builder()
                .reporterMember(reporterMember)
                .reportedMember(targetMember)
                .reportType(req.reportType())
                .body(req.body())
                .build();

        reportRepository.save(report);

        // 사진을 reportPhoto에 저장
        if (req.imageUrls() != null) {
            for (String url : req.imageUrls()) {
                ReportPhoto photo = ReportPhoto.builder()
                        .report(report)
                        .reportPhotoUrl(url)
                        .build();

                reportPhotoRepository.save(photo);
            }
        }

        // reportHistory에 status를 Pending으로 한 채로 저장
        ReportHistory history = ReportHistory.builder()
                .report(report)
                .build();

        reportHistoryRepository.save(history);
    }
}
