package com.briup.cms.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching   //开启缓存功能，作用于缓存配置类上或者作用于springboot启动类上
@Configuration
public class RedisConfig {

    /**
     * 创建一个RedisTemplate实例，用于操作Redis数据库。
     * 其中，redisTemplate是一个泛型为<String, Object>的模板对象，可以存储键值对数据；
     * @param factory   factory是一个Redis连接工厂对象，用于建立与Redis服务器的连接
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //创建一个字符串序列化器对象，用于将字符串类型的数据转换成二进制格式存储到Redis中。
        RedisSerializer<String> redisSerializer =
                new StringRedisSerializer();

        //创建一个字符串序列化器对象，用于将字符串类型的数据转换成二进制格式存储到Redis中。
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer(Object.class);

        //创建一个字符串序列化器对象，用于将字符串类型的数据转换成二进制格式存储到Redis中。
        ObjectMapper om = new ObjectMapper();

        //设置ObjectMapper对象的属性访问器可见性，使其能够访问所有的属性。
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        //启用默认类型识别，避免在序列化过程中出现类型错误。
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        //将ObjectMapper对象设置为JSON序列化器的属性访问器。
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //将ObjectMapper对象设置为JSON序列化器的属性访问器。
        template.setConnectionFactory(factory);

        //key序列化方式,将ObjectMapper对象设置为JSON序列化器的属性访问器。
        template.setKeySerializer(redisSerializer);

        //value序列化,将ObjectMapper对象设置为JSON序列化器的属性访问器。
        template.setValueSerializer(jackson2JsonRedisSerializer);

        //核心代码：修改hash类型 key和value序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }

    /**
     *  创建一个CacheManager实例，用于管理缓存。
     *  其中，cacheManager是一个缓存管理器对象，用于管理缓存的生命周期和策略等；
     * @param factory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        //第一个序列化器用于将字符串类型的数据转换为二进制格式，第二个序列化器用于将Java对象序列化为JSON格式。
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置序列化（解决乱码的问题）,过期时间600秒
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                //禁用缓存空值
                .disableCachingNullValues();
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
}
