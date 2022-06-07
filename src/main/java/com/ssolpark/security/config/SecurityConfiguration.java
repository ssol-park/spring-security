package com.ssolpark.security.config;

import com.ssolpark.security.security.AuthenticationFilter;
import com.ssolpark.security.security.JwtFilter;
import com.ssolpark.security.security.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // JWT 제공
    private final JwtProvider jwtProvider;

    // 인증 실패 or 인증헤더를 전달 받지 못했을 때
    private final AuthenticationEntryPoint authenticationEntryPoint;

    // 인증 성공
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    // 인증 실패
    private final AuthenticationFailureHandler authenticationFailureHandler;

    // 인가 실패
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfiguration(JwtProvider jwtProvider, AuthenticationEntryPoint authenticationEntryPoint
            , AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler
            , AccessDeniedHandler accessDeniedHandler) {
        this.jwtProvider = jwtProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /*
    *  보안 기능 초기화 및 설정
    * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        final String[] POST_WHITELIST = new String[] {
                "/auth/registrations",
                "/auth"
        };

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and().authorizeRequests()
                .antMatchers(POST_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFilter authenticationFilter() throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager());

        authenticationFilter.setFilterProcessesUrl("/auth");

        authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);

        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        authenticationFilter.afterPropertiesSet();

        return authenticationFilter;
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtProvider);
    }
}
