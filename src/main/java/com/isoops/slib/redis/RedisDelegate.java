package com.isoops.slib.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

public interface RedisDelegate {

    StringRedisTemplate getStringRedisTemplate();

}
