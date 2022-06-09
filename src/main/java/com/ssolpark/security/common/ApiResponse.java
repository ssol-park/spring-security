package com.ssolpark.security.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ApiResponse {

    private int code =ApiResponseType.SUCCESS.getCode();

    private String msg = ApiResponseType.SUCCESS.getMessage();

    private Boolean isSuccess = true;

    public ApiResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.isSuccess = code == 200 ? true : false;
    }

    public static ApiResponse error(ApiResponseType apiResponseType) {
        return new ApiResponse(apiResponseType.getCode(), apiResponseType.getMessage());
    }

    public static ApiResponse error(ApiResponseType apiResponseType, String message) {
        return new ApiResponse(apiResponseType.getCode(), message);
    }

    public static ApiResponse error(ApiResponseType apiResponseType, List<ReplaceString> replaceStringList) {

        String message = apiResponseType.getMessage();

        for (ReplaceString replaceString : replaceStringList) {
            message = message.replace(replaceString.getKey(), replaceString.getValue());
        }

        return new ApiResponse(apiResponseType.getCode(), message);
    }

    public static void error(ServletResponse response, ApiResponseType apiResponseType) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        HttpServletResponse servletResponse = (HttpServletResponse) response;
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setStatus(apiResponseType.getCode());
        servletResponse.getWriter().write(Objects.requireNonNull(objectMapper.writeValueAsString(ApiResponse.error(apiResponseType))));
    }

    @Getter
    public static class ReplaceString {

        private final String key;

        private final String value;

        public ReplaceString(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
