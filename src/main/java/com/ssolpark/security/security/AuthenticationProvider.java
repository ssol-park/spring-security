package com.ssolpark.security.security;

import com.ssolpark.security.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final MemberService memberService;

    private final JwtProvider jwtProvider;

    public AuthenticationProvider(MemberService memberService, JwtProvider jwtProvider) {
        this.memberService = memberService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        String jwtToken = authentication.getCredentials().toString();

        if (jwtProvider.isTokenExpired(jwtToken)) {
            throw new AccountExpiredException("TOKEN IS EXPIRED");
        }

        String email = jwtProvider.getSubjectFromToken(jwtToken);

        if (!StringUtils.hasText(email)) {
            throw new UsernameNotFoundException("TOKEN DOES NOT CONTAIN EMAIL");
        }

        // todo 중복호출
        log.info("--- aa");
        UserDetails userDetails = memberService.getMemberByEmail(email).orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND WITH EMAIL"));
        log.info("--- bb");

        return userDetails;
    }
}