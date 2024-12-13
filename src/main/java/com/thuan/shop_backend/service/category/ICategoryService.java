package com.thuan.shop_backend.service.category;

import com.thuan.shop_backend.dto.request.category.CategoryRequest;
import com.thuan.shop_backend.dto.response.category.CategoryResponse;
import com.thuan.shop_backend.entity.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategory(long categoryId);
    Category updateCategory(long categoryId, CategoryRequest categoryRequest);
    void deleteCategory(long categoryId);
}
