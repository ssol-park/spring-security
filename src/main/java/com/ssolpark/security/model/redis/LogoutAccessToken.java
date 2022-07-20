package com.ssolpark.security.model.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

// todo
@Getter
@RedisHash("logoutAccessToken")
public class LogoutAccessToken {

    @Id
    private String id;

    private String email;

    private Long expiration;

    @Builder
    public LogoutAccessToken(String id, String email, Long expiration) {
        this.id = id;
        this.email = email;
        this.expiration = expiration;
    }

    public static LogoutAccessToken of (String accessToken, String email, Long remainingMilliSeconds) {
        return LogoutAccessToken.builder()
                .id(accessToken)
                .email(email)
                .expiration(remainingMilliSeconds)
                .build();
    }
}
