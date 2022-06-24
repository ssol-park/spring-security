package com.ssolpark.security.repository;

import com.ssolpark.security.model.MemberRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, Long> {

    Optional<MemberRefreshToken> findByRefreshToken(String refreshToken);

}
