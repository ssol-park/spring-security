package com.ssolpark.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberJoinDto {

    private String email;

    private String password;

    private String name;

    public MemberJoinDto(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
