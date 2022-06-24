package com.ssolpark.security.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssolpark.security.common.ApiError;
import com.ssolpark.security.common.ResponseType;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        List<ResponseType> responseTypes = Arrays.stream(ResponseType.values()).filter(type -> {
            return type.name().equals(exception.getMessage());
        }).collect(Collectors.toList());

        if(!responseTypes.isEmpty()) {

            ResponseType responseType = responseTypes.get(0);

            response.setStatus(responseType.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiError error = new ApiError(responseType);

            response.getOutputStream()
                    .println(objectMapper.writeValueAsString(error));

        }else {

            response.setStatus(ResponseType.UNAUTHORIZED_RESPONSE.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiError error = new ApiError(ResponseType.UNAUTHORIZED_RESPONSE);

            response.getOutputStream()
                    .println(objectMapper.writeValueAsString(error));

        }

    }
}
