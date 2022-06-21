package com.ssolpark.security.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssolpark.security.common.ApiResponse;
import com.ssolpark.security.common.ApiResponseType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse error = ApiResponse.error(ApiResponseType.UNAUTHORIZED_RESPONSE);

        response.getOutputStream()
                .println(objectMapper.writeValueAsString(error));
    }
}
