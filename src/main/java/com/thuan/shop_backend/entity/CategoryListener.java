package com.thuan.shop_backend.entity;

import com.thuan.shop_backend.service.category.ICategoryRedisService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CategoryListener {
    private final ICategoryRedisService categoryRedisService;

    @PostUpdate
    @PostPersist
    @PostRemove
    public void clearCategoryCache(Category category) {
        categoryRedisService.evictCategoriesCache();
    }
}