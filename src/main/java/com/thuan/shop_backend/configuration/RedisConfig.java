package com.thuan.shop_backend.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Su dung StringRedisSerializer de serialize key
        template.setKeySerializer(new StringRedisSerializer());

        // Sử dụng JdkSerializationRedisSerializer để serialize value
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        return template;
    }

    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();

        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(10)
                .setTimeout(10000);

        return Redisson.create(config);
    }
}
