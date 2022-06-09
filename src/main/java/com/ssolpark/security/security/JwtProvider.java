package com.ssolpark.security.security;

import com.ssolpark.security.dto.auth.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public final class JwtProvider {

    @Value("${accessToken.duration}")
    private long ACCESS_TOKEN_VALID_TIME;

    @Value("${jwt.secret-key}")
    private String secretCode;

    private Key secretKey;

    @PostConstruct
    private void init() {
        secretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(secretCode.getBytes()).getBytes(StandardCharsets.UTF_8));
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public <T>T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {

        final Date expirationDate = getExpirationDateFromToken(token);

        if(expirationDate == null) {
            return false;
        }

        return expirationDate.before(new Date());
    }

    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public JwtResponse generateToken(String email) {

        Map<String, Object> claims = new HashMap<>();

        final String accessToken = doGenerateToken(claims, email, ACCESS_TOKEN_VALID_TIME);

        return JwtResponse.builder().accessToken(accessToken).expireDate(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALID_TIME * 1000)).build();
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long expirationDuration) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationDuration * 1000)).signWith(secretKey)
                .compact();
    }
}
