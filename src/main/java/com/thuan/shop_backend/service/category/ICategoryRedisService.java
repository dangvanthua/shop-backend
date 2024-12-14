package com.thuan.shop_backend.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thuan.shop_backend.dto.response.category.CategoryResponse;

import java.util.List;

public interface ICategoryRedisService {
    List<CategoryResponse> getCategoriesFromCache() throws JsonProcessingException;
    void saveCategoriesToCache(List<CategoryResponse> categories) throws JsonProcessingException;
    void evictCategoriesCache();
}
