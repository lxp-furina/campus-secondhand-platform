package com.campus.secondhand.utils;

import com.campus.secondhand.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private final AppProperties properties;

    public JwtUtils(AppProperties properties) {
        this.properties = properties;
    }

    public String generateToken(Long userId, String role) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + properties.getJwt().getExpireHours() * 3600_000L);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, properties.getJwt().getSecret())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(properties.getJwt().getSecret())
                .parseClaimsJws(token)
                .getBody();
    }
}
