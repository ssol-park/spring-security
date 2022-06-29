package com.ssolpark.security.controller;

import com.ssolpark.security.common.DataApiResponse;
import com.ssolpark.security.dto.auth.ReissueTokenRequest;
import com.ssolpark.security.dto.auth.JwtRequest;
import com.ssolpark.security.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public DataApiResponse login (@RequestBody JwtRequest jwtRequest) {
        return authenticationService.authenticateForJwt(jwtRequest);
    }

    @GetMapping("/kakao")
    public DataApiResponse kakaoLogin (@RequestParam String code) {

        return authenticationService.getKakaoAccessToken(code);
    }

    @PostMapping("/issue")
    public DataApiResponse reIssueAccessToken (@RequestBody ReissueTokenRequest tokenRequest) {
        return authenticationService.reIssueAccessToken(tokenRequest);
    }
}
