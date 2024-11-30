package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.CategoryRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.CategoryResponse;
import com.thuan.shop_backend.service.category.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
        return ApiResponse.<CategoryResponse>builder()
                .message("Create category success")
                .result(categoryResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categoryResponses = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Get all categories success")
                .result(categoryResponses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategory(
            @PathVariable("id") long categoryId) {
        CategoryResponse categoryResponse = categoryService.getCategory(categoryId);
        return ApiResponse.<CategoryResponse>builder()
                .message("Get category success")
                .result(categoryResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable("id") long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.updateCategory(
                categoryId, categoryRequest);
        return ApiResponse.<CategoryResponse>builder()
                .message("Update category success")
                .result(categoryResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable("id") long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .message("Delete category success")
                .build();
    }
}
