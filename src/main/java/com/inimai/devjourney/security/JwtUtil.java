package com.inimai.devjourney.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(
            secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }
    public String generateToken(String email) {
        return Jwts.builder()//starts token
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey())
            .compact();
    }
    public String extractEmail(String token) {
        return Jwts.parser()//reads token
            .verifyWith(getSigningKey())//signature
            .build()//builds final token
            .parseSignedClaims(token)//check sign if ok then or throw error
            .getPayload()//the middle part is payload xxxx.yyyy.zzzz
            .getSubject();
    }
}