package com.ssolpark.security.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ReissueTokenRequest{

    private String email;

    private String accessToken;

    private String refreshToken;

    public ReissueTokenRequest(String email, String accessToken, String refreshToken) {
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
