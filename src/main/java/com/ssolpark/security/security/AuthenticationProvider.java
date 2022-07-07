package com.ssolpark.security.security;

import com.ssolpark.security.common.ResponseType;
import com.ssolpark.security.repository.MemberRefreshTokenRepository;
import com.ssolpark.security.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final MemberService memberService;

    private final MemberRefreshTokenRepository refreshTokenRepo;

    private final JwtProvider jwtProvider;

    public AuthenticationProvider(MemberService memberService, MemberRefreshTokenRepository refreshTokenRepo, JwtProvider jwtProvider) {
        this.memberService = memberService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        String token = authentication.getCredentials().toString();

        if(jwtProvider.validateToken(token)) {
            throw new AccountExpiredException(ResponseType.UNAUTHORIZED_RESPONSE.name());
        }

        String email = jwtProvider.getSubjectFromToken(username);

        UserDetails userDetails = memberService.getMemberByEmail(email).orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND WITH EMAIL"));

        return userDetails;
    }
}