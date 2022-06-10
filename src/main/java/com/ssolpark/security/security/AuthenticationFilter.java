package com.ssolpark.security.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String AUTHORIZATION_TYPE = "Bearer ";

    public AuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        final String requestToken = request.getHeader(AUTHORIZATION_HEADER);

        if(requestToken != null && requestToken.startsWith(AUTHORIZATION_TYPE)) {

            String jwtToken = requestToken.substring(7);

            Authentication requestAuth = new UsernamePasswordAuthenticationToken(jwtToken, jwtToken);

            return getAuthenticationManager().authenticate(requestAuth);

        }

        // todo exception handling
        throw  new AuthenticationCredentialsNotFoundException("LOGIN TOKEN NOT FOUND");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}
