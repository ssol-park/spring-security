package com.ssolpark.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SnsUserInfoDto {

    private Long id;

    private String email;

    @Builder
    public SnsUserInfoDto(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
