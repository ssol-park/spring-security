package com.ssolpark.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegMemberDto {

    private Long kakaoId;

    private String email;

    private String password;

    private String name;

    public RegMemberDto(String email, String password, String name) {
        this.kakaoId = null;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Builder
    public RegMemberDto(Long kakaoId, String email) {
        this.kakaoId = kakaoId;
        this.email = email;
    }
}
