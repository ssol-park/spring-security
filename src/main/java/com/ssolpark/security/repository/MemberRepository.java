package com.ssolpark.security.repository;

import com.ssolpark.security.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
