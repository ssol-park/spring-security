package com.ssolpark.security.service;

import com.ssolpark.security.common.ApiResponse;
import com.ssolpark.security.common.DataApiResponse;
import com.ssolpark.security.dto.RegMemberDto;
import com.ssolpark.security.dto.auth.JwtRequest;

public interface AuthenticationService {

    ApiResponse registration(RegMemberDto regMemberDto);

    DataApiResponse authenticateForJwt(JwtRequest authRequest);
}
