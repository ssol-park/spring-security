package com.ssolpark.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegMemberDto {

    private String email;

    private String password;

    private String name;

    public RegMemberDto(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

}
