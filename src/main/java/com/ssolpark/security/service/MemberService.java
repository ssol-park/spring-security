package com.ssolpark.security.service;

import com.ssolpark.security.security.UserDetailsImpl;

import java.util.Optional;

public interface MemberService {

    Optional<UserDetailsImpl> getMemberByEmail(String email);
}
