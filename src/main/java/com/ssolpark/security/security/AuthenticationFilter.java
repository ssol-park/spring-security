package com.ssolpark.security.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String AUTHORIZATION_TYPE = "Bearer ";

    public AuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        final String requestToken = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(requestToken) && requestToken.startsWith(AUTHORIZATION_TYPE)) {

            String jwtToken = requestToken.substring(7);

            Authentication requestAuth = new UsernamePasswordAuthenticationToken(jwtToken, jwtToken);

            return getAuthenticationManager().authenticate(requestAuth);
        }

        throw new AuthenticationCredentialsNotFoundException("LOGIN TOKEN NOT FOUND");
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain chain,
                                            final Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }
}
