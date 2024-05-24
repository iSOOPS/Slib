package com.isoops.slib.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisDelegateDefaultImpl implements RedisDelegate{
    @Override
    public StringRedisTemplate getStringRedisTemplate() {
        throw new UnsupportedOperationException("未配置 redis");
    }
}
