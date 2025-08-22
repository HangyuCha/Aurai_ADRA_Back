package com.metaverse.aurai_adra.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key = io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long tokenValidityInMilliseconds = 3600000L;

    public String createToken(String nickname) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);
        return Jwts.builder()
                .setSubject(nickname)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getNicknameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // 선택: Authorization 헤더 파서
    public String resolveToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}