package com.baojia.user_manager.security.token;

import com.baojia.user_manager.config.JwtProperties;
import com.baojia.user_manager.model.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * JWT 签发与解析（HS256）。
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public String createAccessToken(SysUser user, List<String> roleCodes) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.getAccessTokenTtlSeconds());
        SecretKey key = signingKey();
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(String.valueOf(user.getUserId()))
                .claim("username", user.getUsername())
                .claim("roleCodes", roleCodes == null ? List.of() : roleCodes)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parseAndValidate(String token) throws ExpiredJwtException, JwtException {
        return Jwts.parser()
                .verifyWith(signingKey())
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret 长度须至少 32 字节以满足 HS256");
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}
