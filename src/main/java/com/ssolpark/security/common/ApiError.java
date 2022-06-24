package com.ssolpark.security.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiError extends ApiResponse{

    private HttpStatus status;

    public ApiError(ResponseType responseType) {
        this(responseType.getHttpStatus(), responseType.getMessage(), responseType.getCode());
    }

    public ApiError(HttpStatus status, String message, int errorCode) {
        super(errorCode, message);
        this.status = status;
    }

}
