package com.example.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @date 2022/8/2- 18:33
 */
@Configuration
public class redisConfig extends CachingConfigurerSupport {

    @Bean
   public  RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){

       RedisTemplate<Object, Object> template = new RedisTemplate<>();

       //默认序列化为JdkSerializationRedisSerializer
       template.setKeySerializer(new StringRedisSerializer());
       template.setHashKeySerializer(new StringRedisSerializer());
       template.setConnectionFactory(redisConnectionFactory);

       return template;

   }

}
