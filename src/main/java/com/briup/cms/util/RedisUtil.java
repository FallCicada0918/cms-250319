package com.briup.cms.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
@Component
public class RedisUtil {
 
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
 
    // 指定缓存失效时间
    public Boolean expire(final String key, final long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 
    // 根据键获取值
    public Object get(final String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
 
    // 将<key, value>键值对存入redis
    public Boolean set(final String key, final Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hash添加操作：将<key, value>键值对存入redis
    public Boolean hset(final String key, final String hashKey ,final Object value) {
        try {
            redisTemplate.opsForHash().put(key,hashKey,value);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Hash获取操作：获取Hash里的值
    public Map<Object, Object> getHash(String key){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return entries;
    }
 
    // 将键值对存入value并设置过期时间
    public Boolean set(final String key, final Object value, final long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 
    // 删除键
    public Boolean del(final String key) {
        try {
//            redisTemplate.opsForValue().getAndDelete(key);
            redisTemplate.delete(key);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hash添加操作：键自增【业务相关】
    public Integer increment(final String key, final String hashKey) {
        //如果key不存在，则新增并赋初始值为0
        if(!redisTemplate.opsForHash().hasKey(key,hashKey)) {
            hset(key,hashKey,0);
        }
        //浏览量自增
        Integer view_count = Math.toIntExact(redisTemplate.opsForHash().increment(key,hashKey,1));
        return view_count;
    }
}