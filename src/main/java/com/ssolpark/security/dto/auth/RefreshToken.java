package com.ssolpark.security.dto.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private Long memberId;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    @Builder
    public RefreshToken(Long id, String refreshToken, Long expiration) {
        this.memberId = id;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public static RefreshToken createRefreshToken(Long memberId, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
                .id(memberId)
                .refreshToken(refreshToken)
                .expiration(remainingMilliSeconds)
                .build();
    }
}
