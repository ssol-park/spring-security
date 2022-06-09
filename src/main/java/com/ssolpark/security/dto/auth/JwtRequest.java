package com.ssolpark.security.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtRequest {

    private String email;

    private String password;

    private String refreshToken;

    @Builder
    public JwtRequest(String email, String password, String refreshToken) {
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
    }
}
