package com.ssolpark.security.service;

import com.ssolpark.security.common.ApiResponse;
import com.ssolpark.security.dto.MemberJoinDto;

public interface MemberService {
    ApiResponse registration(MemberJoinDto memberJoinDto);
}
