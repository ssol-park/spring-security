package com.ssolpark.security.controller;

import com.ssolpark.security.common.ApiResponse;
import com.ssolpark.security.common.ResponseType;
import com.ssolpark.security.dto.RegMemberDto;
import com.ssolpark.security.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final AuthenticationService authenticationService;

    public RegistrationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ApiResponse registrations(@RequestBody RegMemberDto regMemberDto) {
        authenticationService.registration(regMemberDto);
        return new ApiResponse(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage());
    }

}
