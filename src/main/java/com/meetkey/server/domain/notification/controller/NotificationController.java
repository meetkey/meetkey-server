package com.meetkey.server.domain.notification.controller;

import com.meetkey.server.domain.notification.service.NotificationService;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;

    @Operation(summary = "알림 읽음 처리 API", description = "특정 알림을 읽음 처리합니다. (매칭/시스템 알림용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "MEMBER4041: 해당 사용자를 찾을 수 없습니다. , " +
                    "NOTIFICATION4041: 해당 알림을 찾을 수 없습니다. , " +
                    "NOTIFICATION4031: 해당 사용자의 알림이 아닙니다.")
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @Parameter(description = "알림 ID", example = "1") @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.readNotification(notificationId, userDetails.getMemberId());
        return ResponseEntity.ok().build();
    }
}
