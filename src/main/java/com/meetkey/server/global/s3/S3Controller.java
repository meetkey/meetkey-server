package com.meetkey.server.global.s3;

import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {
    private final S3Service s3Service;

    @Operation(summary = "업로드용 Presigned Url 발급 API", description = "프론트에서 파일 업로드 전에 요청")
    @GetMapping("/presigned-upload")
    public BasicResponse<S3ResDTO.PresignedUrl> getPresignedUploadUrl(
            @RequestParam String folder,
            @RequestParam String fileName,
            @RequestParam String contentType
    ){
        S3ResDTO.PresignedUrl response = s3Service.generateUploadPresignedUrl(folder, fileName, contentType);
        return BasicResponse.success(CommonSuccessStatus._OK, response);
    }
}
