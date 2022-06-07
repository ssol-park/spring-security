package com.ssolpark.security.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationDto {

    private String email;

    private String password;

    @Builder
    public AuthenticationDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
