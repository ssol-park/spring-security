package com.ssolpark.security.common;

import lombok.Getter;

@Getter
public class DataApiResponse<T> extends ApiResponse{

    private T data;

    public DataApiResponse(T data) {
        super(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage());
        this.data = data;
    }
}
