package com.ssolpark.security.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse {

    private int code = ResponseType.SUCCESS.getCode();

    private String msg = ResponseType.SUCCESS.getMessage();

    private Boolean isSuccess = true;

    public ApiResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.isSuccess = code == 200 ? true : false;
    }
}
