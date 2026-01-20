package com.meetkey.server.global.security.oauth;

import com.meetkey.server.domain.auth.exception.AuthErrorStatus;
import com.meetkey.server.domain.auth.exception.AuthException;
import com.meetkey.server.global.security.oauth.dto.OidcDTO;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
public class JwtOidcProvider {
    private final String KID = "kid";

    public String getKidFromIdTokenHeader(String token, String iss, String aud, String nonce){
        return (String) getIdTokenClaims(token, iss, aud, nonce).getHeader().get(KID);
    }

    private Jwt<Header, Claims> getIdTokenClaims(String token, String iss, String aud, String nonce){
        try {
            return Jwts.parser()
                    .requireAudience(aud) // aud 검증 (app id)
                    .requireIssuer(iss) // iss 검증 (카카오)
                    .require("nonce", nonce) // nonce 검증
                    .build()
                    .parseUnsecuredClaims(removeSigFromIdToken(token));
        } catch (Exception e){
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        }
    }

    /*
     * idToken의 서명 제거 메서드
     */
    private String removeSigFromIdToken(String token){
        String[] splitToken = token.split("\\.");
        if (splitToken.length != 3) throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        return splitToken[0] + "." + splitToken[1] + "."; // Header, Payload 추출
    }

    /*
     * 공개키로 서명 검증해서 iss, aud, sub 을 return.
     * n: 공개키 모듈
     * e: 공개키 지수
     */
    public OidcDTO.OIDCDecodePayload getOIDCTokenBody(String token, String n, String e){
        Claims body = getOIDCTokenJws(token, n, e).getPayload();
        return new OidcDTO.OIDCDecodePayload(
                body.getIssuer(),
                body.getAudience(),
                body.getSubject()
        );
    }

    private Jws<Claims> getOIDCTokenJws(String token, String n, String e){
        try{
            return Jwts.parser()
                    .verifyWith(getRSAPublicKey(n, e)) // 카카오의 경우 RS256 알고리즘으로 고정
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception ex){
            throw new AuthException(AuthErrorStatus.INVALID_TOKEN);
        }
    }

    private PublicKey getRSAPublicKey(String n, String e) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodeN = Base64.getUrlDecoder().decode(n);
        byte[] decodeE = Base64.getUrlDecoder().decode(e);
        BigInteger nn = new BigInteger(1, decodeN);
        BigInteger ee = new BigInteger(1, decodeE);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(nn, ee);
        return keyFactory.generatePublic(publicKeySpec);
    }
}
