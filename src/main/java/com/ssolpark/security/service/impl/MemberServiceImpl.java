package com.ssolpark.security.service.impl;

import com.ssolpark.security.common.ApiResponse;
import com.ssolpark.security.common.ApiResponseType;
import com.ssolpark.security.dto.MemberJoinDto;
import com.ssolpark.security.model.Member;
import com.ssolpark.security.repository.MemberRepository;
import com.ssolpark.security.service.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ApiResponse registration(MemberJoinDto memberJoinDto) {

        Member member = memberRepository.findByEmail(memberJoinDto.getEmail()).orElse(null);

        if(member != null) {
            return new ApiResponse(ApiResponseType.ALREADY_DATA_RESPONSE.getCode(), ApiResponseType.ALREADY_DATA_RESPONSE.getMessage());
        }

        Member saveMember = Member.builder()
                .email(memberJoinDto.getEmail())
                .password(passwordEncoder.encode(memberJoinDto.getPassword()))
                .name(memberJoinDto.getName())
                .build();

        memberRepository.save(saveMember);

        return new ApiResponse(ApiResponseType.SUCCESS.getCode(), ApiResponseType.SUCCESS.getMessage());
    }
}
