package com.ssolpark.security.dto.auth;

import com.ssolpark.security.constant.GrantType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtRequest {

    private String email;

    private String password;

    private String refreshToken;

    private GrantType grantType;

}
