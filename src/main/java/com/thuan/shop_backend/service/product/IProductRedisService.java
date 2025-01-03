package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.dto.response.product.ProductResponse;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface IProductRedisService {
    String getCacheKey(String keyword);
    Optional<String> getFromCache(String key);
    void saveToCache(String key, String value);
    void deleteCache(String key);
    boolean acquireLock(String lockKey, String lockValue, int expireTime);
    void releaseLock(String lockKey, String lockValue);
}
