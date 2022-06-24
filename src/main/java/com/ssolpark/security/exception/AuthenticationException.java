package com.ssolpark.security.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationException(String msg) {
        super(msg);
    }
}
