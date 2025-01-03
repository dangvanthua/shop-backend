package com.thuan.shop_backend.service.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProductRedisService implements IProductRedisService{

    @Value("${cache.product.ttl}")
    private int cacheTtl;

    private final RedisTemplate<String, String> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ProductRedisService.class);

    public ProductRedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getCacheKey(String keyword) {
        return "search:product:v1:keyword:" + keyword;
    }

    @Override
    public Optional<String> getFromCache(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void saveToCache(String key, String value) {
        redisTemplate.opsForValue().set(key, value, cacheTtl, TimeUnit.SECONDS);
    }

    @Override
    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean acquireLock(String lockKey, String lockValue, int expireTime) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS));
    }

    @Override
    public void releaseLock(String lockKey, String lockValue) {

        RedisScript<Long> RELEASE_LOCK_SCRIPT = RedisScript.of(
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "   return redis.call('del', KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end",
                Long.class
        );

        // Sử dụng String key và value
        Long result = redisTemplate.execute(
                RELEASE_LOCK_SCRIPT,
                List.of(lockKey),  // KEYS[1]
                lockValue          // ARGV[1]
        );

        if (result != null && result > 0) {
            logger.info("Lock released successfully for key: {}", lockKey);
        } else {
            logger.warn("Failed to release lock for key: {}", lockKey);
        }
    }
}
