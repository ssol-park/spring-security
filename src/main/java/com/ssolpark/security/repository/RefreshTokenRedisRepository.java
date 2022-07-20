package com.ssolpark.security.repository;

import com.ssolpark.security.model.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}
