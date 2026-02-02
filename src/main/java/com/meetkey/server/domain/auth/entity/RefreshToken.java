package com.meetkey.server.domain.auth.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
public class RefreshToken {
    @Id
    private String refreshToken;
    private String memberId;

    public RefreshToken(final String refreshToken, final String memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
