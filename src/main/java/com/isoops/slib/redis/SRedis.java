package com.isoops.slib.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.isoops.slib.utils.SUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class SRedis {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public boolean set(String key, Object obj) {
        return set(key,obj,null,null);
    }

    public boolean set(String key, Object obj ,Long seconds) {
        return set(key,obj, seconds,TimeUnit.SECONDS);
    }

    private boolean set(String key, Object obj ,Long time,TimeUnit unit) {
        if (SUtil.isBlank(key,obj)){
            return false;
        }
        try {
            if (time == null || unit == null){
                stringRedisTemplate.opsForValue().set(key, (obj instanceof String) ? (String) obj : JSON.toJSONString(obj));
            }
            else {
                stringRedisTemplate.opsForValue().set(key, (obj instanceof String) ? (String) obj : JSON.toJSONString(obj), time, unit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String get(String key){
        if (SUtil.isBlank(key)){
            return null;
        }
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T>T get(String key, Class<T> tClass){
        if (SUtil.isBlank(key) || tClass == String.class){
            return null;
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null){
            return null;
        }
        return JSON.parseObject(value,tClass);
    }
    public <T>List<T> get(Collection<String> keys, Class<T> tClass) {
        if (SUtil.isBlank(keys) || keys.size()<1){
            return null;
        }
        List<String> strings = stringRedisTemplate.opsForValue().multiGet(keys);
        List<T> response = new ArrayList<>();
        if (strings != null) {
            for (String json : strings){
                response.add(JSON.parseObject(json,tClass));
            }
        }
        return response;
    }

    public <T>List<T> getList(String key, Class<T> tClass){
        if (SUtil.isBlank(key)){
            return null;
        }
        String value = stringRedisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(value,tClass);
    }

    public boolean delete(String key) {
        if (SUtil.isBlank(key)){
            return false;
        }
        try {
            stringRedisTemplate.delete(key);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String ...arg) {
        List<String> list = Arrays.asList(arg);
        if (list.size()<1){
            return false;
        }
        try {
            stringRedisTemplate.delete(list);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(List<String> list) {
        if (null == list || list.size()<1){
            return false;
        }
        try {
            stringRedisTemplate.delete(list);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 增加(自增长), 负数则为自减
     */
    public Long incr(String key,Long increment){
        if (SUtil.isBlank(key,increment)){
            return null;
        }
        return stringRedisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key,Long time,TimeUnit unit){
        if (SUtil.isBlank(key,time,unit)){
            return false;
        }
        try {
            return stringRedisTemplate.expire(key,time,unit);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     */
    public Boolean persist(String key) {
        if (SUtil.isBlank(key)){
            return false;
        }
        return stringRedisTemplate.persist(key);
    }

    /**
     * 返回 key 的剩余的过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        if (SUtil.isBlank(key,unit)){
            return null;
        }
        return stringRedisTemplate.getExpire(key, unit);
    }

    /**
     * 检查key是否存在
     */
    public boolean hasKey(String key) {
        if (SUtil.isBlank(key)){
            return false;
        }
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 查找匹配的key
     */
    public Set<String> getKeys(String pattern) {
        if (SUtil.isBlank(pattern)){
            return null;
        }
        return stringRedisTemplate.keys(pattern);
    }



    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁 /当key不存在当时候才会成功
     */
    public boolean setnx(String key ,String value ,Long time,TimeUnit unit){
        if (SUtil.isBlank(key,value,time,unit)){
            return false;
        }
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, time, unit);
    }

    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁
     * 解锁key
     */
    public boolean deletenx(String key){
        if (SUtil.isBlank(key)){
            return false;
        }
        return stringRedisTemplate.opsForValue().getOperations().delete(key);
    }

}
