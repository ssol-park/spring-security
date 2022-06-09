package com.ssolpark.security.repository;

import com.ssolpark.security.model.MemberRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {
}
