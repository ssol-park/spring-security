package com.ssolpark.security.exception.handler;

import com.ssolpark.security.common.ApiError;
import com.ssolpark.security.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiError> handleBusinessException(BusinessException ex) {
        ApiError apiErr = new ApiError(ex.getStatus(), ex.getMessage(), ex.getErrorType().getCode());
        return new ResponseEntity<>(apiErr, apiErr.getStatus());
    }

}
