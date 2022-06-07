package com.ssolpark.security.common;

import lombok.Getter;

@Getter
public class DataApiResponse<T> extends ApiResponse{

    private T result;

    public DataApiResponse(T result) {
        super(ApiResponseType.SUCCESS.getCode(), ApiResponseType.SUCCESS.getMessage());
        this.result = result;
    }
}
