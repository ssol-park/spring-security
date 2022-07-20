package com.ssolpark.security.service;

import java.time.Duration;

public interface RedisService {

    void setValues(String key, String data);

    void setValues(String key, String data, Duration duration);

    String getValues(String key);

    void deleteValues(String key);
}
