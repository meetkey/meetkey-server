package com.meetkey.server.global.s3;

import lombok.Builder;

public class S3ResDTO {
    @Builder
    public record PresignedUrl(
            String url,
            String key
    ){}
}
