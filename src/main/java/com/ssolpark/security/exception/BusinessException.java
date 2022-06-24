package com.ssolpark.security.exception;

import com.ssolpark.security.common.ResponseType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private HttpStatus status;

    private String message;

    private ResponseType errorType;

    public BusinessException(ResponseType errorType) {
        this.errorType = errorType;
        this.status = errorType.getHttpStatus();
        this.message = errorType.getMessage();
    }
}
