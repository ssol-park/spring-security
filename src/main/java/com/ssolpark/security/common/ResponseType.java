package com.ssolpark.security.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseType {

    SUCCESS(200, HttpStatus.OK,"Success"),

    UNAUTHORIZED_RESPONSE(401, HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN_RESPONSE(403, HttpStatus.FORBIDDEN,"Forbidden"),
    NOT_FOUND_RESPONSE(404, HttpStatus.NOT_FOUND,"Not Found"),
    METHOD_NOT_ALLOWED_RESPONSE(405, HttpStatus.METHOD_NOT_ALLOWED,"Method Not Allowed"),

    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    REGISTERED_MEMBER(600, HttpStatus.BAD_REQUEST, "이미 등록된 회원 입니다."),

    JWT_EXPIRED(10000,HttpStatus.BAD_REQUEST, "JWT has been expired");

    private final int code;

    private final HttpStatus httpStatus;

    private final String message;

    ResponseType(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
