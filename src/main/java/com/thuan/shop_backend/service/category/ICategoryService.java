package com.thuan.shop_backend.service.category;

import com.thuan.shop_backend.dto.request.CategoryRequest;
import com.thuan.shop_backend.dto.response.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategory(long categoryId);
    CategoryResponse updateCategory(long categoryId, CategoryRequest categoryRequest);
    void deleteCategory(long categoryId);
}
