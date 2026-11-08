package com.publicconsult.platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long expirationMillis;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-hours}") long hours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = hours * 60 * 60 * 1000;
    }

    public String create(AuthUser user) {
        Date now = new Date();
        return Jwts.builder().setSubject(String.valueOf(user.id()))
                .claim("username", user.username()).claim("role", user.role())
                .setIssuedAt(now).setExpiration(new Date(now.getTime() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public AuthUser parse(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return new AuthUser(Long.valueOf(claims.getSubject()),
                claims.get("username", String.class), claims.get("role", String.class));
    }
}
