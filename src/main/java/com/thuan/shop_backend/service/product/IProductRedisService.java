package com.thuan.shop_backend.service.product;

import com.thuan.shop_backend.dto.response.product.ProductResponse;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface IProductRedisService {
    String getCacheKey(String keyword, int page, int size);
    Optional<String> getFromCache(String key);
    List<ProductResponse> getProductFromCache(String cachedProducts);
    void saveToCache(String key, Object value, Duration ttl);
    void deleteCache(String key);
}
