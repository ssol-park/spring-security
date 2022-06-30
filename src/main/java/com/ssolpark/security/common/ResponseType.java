package com.ssolpark.security.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseType {

    SUCCESS(200, HttpStatus.OK,"Success"),

    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "Bad Request"),
    UNAUTHORIZED_RESPONSE(401, HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN_RESPONSE(403, HttpStatus.FORBIDDEN,"Forbidden"),
    NOT_FOUND_RESPONSE(404, HttpStatus.NOT_FOUND,"Not Found"),
    METHOD_NOT_ALLOWED_RESPONSE(405, HttpStatus.METHOD_NOT_ALLOWED,"Method Not Allowed"),

    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    REGISTERED_MEMBER(600, HttpStatus.BAD_REQUEST, "이미 등록된 회원 입니다."),
    WRONG_EMAIL_OR_PASSWORD(601, HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(602, HttpStatus.BAD_REQUEST, "일치하는 회원 정보를 찾을 수 없습니다."),
    GRANT_TYPE_NOT_FOUND(603, HttpStatus.BAD_REQUEST, "Grant type can't be null"),

    JWT_EXPIRED(10000, HttpStatus.BAD_REQUEST, "JWT has been expired"),
    TOKEN_CANNOT_BE_ISSUED(10001, HttpStatus.BAD_REQUEST, "JWT has not expired"),
    REFRESH_TOKEN_NOT_FOUND(10002, HttpStatus.BAD_REQUEST, "Refresh token not found"),
    REFRESH_TOKEN_EXPIRED(10003, HttpStatus.BAD_REQUEST, "Refresh token has been expired"),

    KAKAO_LOGIN_FAILED(11000, HttpStatus.BAD_REQUEST, "kakao login failed");

    private final int code;

    private final HttpStatus httpStatus;

    private final String message;

    ResponseType(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
