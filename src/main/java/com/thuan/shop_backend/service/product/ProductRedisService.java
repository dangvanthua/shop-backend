package com.thuan.shop_backend.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thuan.shop_backend.dto.response.product.ProductResponse;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class ProductRedisService implements IProductRedisService{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper redisObjectMapper;

    @Override
    public String getCacheKey(String keyword, int page, int size) {
        return "products:" + keyword + ":page:" + page + ":size:" + size;
    }

    @Override
    public Optional<String> getFromCache(String key) {
        return Optional.ofNullable((String) redisTemplate.opsForValue().get(key));
    }

    @Override
    public List<ProductResponse> getProductFromCache(String cachedProducts) {
        try {
            return redisObjectMapper.readValue(
                    cachedProducts,
                    new TypeReference<List<ProductResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.READ_REDIS_FAILED);
        }
    }

    @Override
    public void saveToCache(String key, Object value, Duration ttl) {
        try {
            String jsonValue = redisObjectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttl);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.WRITE_REDIS_FAILED);
        }
    }

    @Override
    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }
}
