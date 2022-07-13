package com.ssolpark.security.dto.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    @Builder
    public RefreshToken(String id, String refreshToken, Long expiration) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public static RefreshToken createRefreshToken(String email, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
                .id(email)
                .refreshToken(refreshToken)
                .expiration(remainingMilliSeconds)
                .build();
    }
}
