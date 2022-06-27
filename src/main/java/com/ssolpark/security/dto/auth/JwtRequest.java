package com.ssolpark.security.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssolpark.security.constant.GrantType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Getter
@NoArgsConstructor
public class JwtRequest {

    private String email;

    private String password;

    private GrantType grantType;

    @JsonCreator
    public JwtRequest(@JsonProperty("email") String email, @JsonProperty("password") String password,
                      @JsonProperty("grantType") String grantType) {
        this.email = email;
        this.password = password;
        this.grantType = convertEnumType(grantType);
    }

    private GrantType convertEnumType(String type) {
        return Arrays.stream(GrantType.values()).filter(grantType -> grantType.name().equals(type)).findFirst().orElseGet(() -> GrantType.NONE);
    }
}
