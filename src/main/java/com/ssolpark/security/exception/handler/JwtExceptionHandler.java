package com.ssolpark.security.exception.handler;

import com.ssolpark.security.common.ApiError;
import com.ssolpark.security.common.ResponseType;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<ApiError> handleExpiredJwtException(ExpiredJwtException e) {
        ApiError error = new ApiError(ResponseType.JWT_EXPIRED);

        return new ResponseEntity<>(error, error.getStatus());
    }

}
