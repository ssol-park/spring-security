package com.ssolpark.security.service.impl;

import com.ssolpark.security.model.Member;
import com.ssolpark.security.repository.MemberRepository;
import com.ssolpark.security.security.UserDetailsImpl;
import com.ssolpark.security.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public Optional<UserDetailsImpl> getMemberByEmail(String email) {

        Member member = memberRepository.findByEmail(email).orElse(null);

        if(member == null) {
            return Optional.empty();
        }

        return Optional.of(new UserDetailsImpl(member));
    }
}
