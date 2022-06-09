package com.ssolpark.security.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class JwtResponse {

    private final String accessToken;

    private final Date expireDate;

    private String refreshToken;

    @Builder
    public JwtResponse(String accessToken, Date expireDate) {
        this.accessToken = accessToken;
        this.expireDate = expireDate;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
