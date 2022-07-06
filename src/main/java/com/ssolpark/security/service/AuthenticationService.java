package com.ssolpark.security.service;

import com.ssolpark.security.common.DataApiResponse;
import com.ssolpark.security.dto.RegMemberDto;
import com.ssolpark.security.dto.auth.ReissueTokenRequest;
import com.ssolpark.security.dto.auth.JwtRequest;
import com.ssolpark.security.model.Member;

public interface AuthenticationService {

    Member registration(RegMemberDto regMemberDto);

    DataApiResponse authenticateForJwt(JwtRequest authRequest);

    DataApiResponse reIssueAccessToken(ReissueTokenRequest tokenRequest);

    DataApiResponse getKakaoAccessToken(String code);
}
