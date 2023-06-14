package cn.molu.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis序列化配置类
 *
 * @author 陌路
 * @Description
 * @date 2022-05-02 上午1:55:50
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lcf) {
        RedisTemplate<String, Object> restTemplate = new RedisTemplate<>();
        // 为String类型的key设置序列化
        restTemplate.setKeySerializer(new StringRedisSerializer());
        // 为String类型的value设置序列化
        restTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 为Hash类型的key设置序列化
        restTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 为Hash类型的value设置序列化
        restTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        restTemplate.setConnectionFactory(lcf);
        return restTemplate;
    }
}