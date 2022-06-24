package com.ssolpark.security.security;

import com.ssolpark.security.common.ResponseType;
import com.ssolpark.security.dto.auth.JwtResponse;
import com.ssolpark.security.exception.AuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
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
        Claims claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);
    }

    public <T>T getClaimFromToken(Function<Claims, T> claimsResolver, final Claims claims) {
        return claimsResolver.apply(claims);
    }

    public Date getExpirationDateFromToken(String token, Claims claims) {
        return getClaimFromToken(Claims::getExpiration, claims);
    }

    public Boolean validateToken(String token) {

        try {

            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

            final Date expirationDate = getExpirationDateFromToken(token, claims);

            if(expirationDate == null) {
                return false;
            }

            return expirationDate.before(new Date());

        }catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {

            log.error(" ### VALIDATION JWT ERROR : {} ### ", e.getLocalizedMessage());
            throw new AuthenticationException(ResponseType.UNAUTHORIZED_RESPONSE.name());

        }catch (ExpiredJwtException e) {

            log.error( " ### EXPIRED JWT ERROR : {}  ###", ResponseType.JWT_EXPIRED.getMessage());
            throw new AuthenticationException(ResponseType.JWT_EXPIRED.name());
        }
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
