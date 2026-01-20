package com.meetkey.server.global.security.jwt;


import com.meetkey.server.domain.auth.exception.AuthErrorStatus;
import com.meetkey.server.domain.auth.exception.AuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret ) {
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(new Date());
    }

    public String createJwt(String category, String username, String role, Long expiredMs){
        return Jwts.builder()
                .subject(username)
                .claim("category", category)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }

    public void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        }

        // refresh token 토큰 만료 검증
        try {
            this.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorStatus.EXPIRED_TOKEN);
        }

        // 토큰이 refresh인지 확인
        String category = this.getCategory(refreshToken);

        if (!category.equals("refresh")){
            throw new  AuthException(AuthErrorStatus.INVALID_TOKEN);
        }
    }
}
