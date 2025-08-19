package com.metaverse.aurai_adra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // Spring 빈으로 등록
public class JwtTokenProvider {

    // JWT 서명을 위한 시크릿 키 (실제 서비스에서는 환경 변수 등으로 관리해야 함)
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 만료 시간 (예: 1시간)
    private final long tokenValidityInMilliseconds = 3600000;

    // JWT 토큰 생성
    public String createToken(String nickname) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(nickname)              // 닉네임을 Subject로 저장
                .setIssuedAt(now)                  // 발급 시간
                .setExpiration(validity)           // 만료 시간
                .signWith(this.key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    // JWT 토큰에서 닉네임 추출
    public String getNicknameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (ExpiredJwtException e) {
            return false; // 만료된 토큰
        } catch (Exception e) {
            return false; // 변조되었거나 잘못된 토큰
        }
    }
}
