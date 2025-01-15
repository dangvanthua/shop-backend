package com.thuan.shop_backend.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thuan.shop_backend.dto.response.category.CategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class CategoryRedisService implements ICategoryRedisService{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper redisObjectMapper;

    @Value("${cache.product.ttl}")
    private int cacheTtl;

    private final String CATEGORY_CACHE_KEY = "categories";

    @Override
    public List<CategoryResponse> getCategoriesFromCache() throws JsonProcessingException {
        String json = (String) redisTemplate.opsForValue().get(CATEGORY_CACHE_KEY);
        return json != null ?
                redisObjectMapper.readValue(json, new TypeReference<List<CategoryResponse>>() {})
                : null;
    }

    @Override
    public void saveCategoriesToCache(List<CategoryResponse> categories) throws JsonProcessingException {
        String json = redisObjectMapper.writeValueAsString(categories);
        redisTemplate.opsForValue().set(CATEGORY_CACHE_KEY, json, Duration.ofSeconds(cacheTtl));
    }

    @Override
    public void evictCategoriesCache() {
        redisTemplate.delete(CATEGORY_CACHE_KEY);
    }
}
