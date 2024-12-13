package com.thuan.shop_backend.service.category;

import com.thuan.shop_backend.dto.request.category.CategoryRequest;
import com.thuan.shop_backend.dto.response.category.CategoryResponse;
import com.thuan.shop_backend.entity.Category;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryRequest categoryRequest) {

        boolean isCategoryExist = categoryRepository.existsByName(categoryRequest.getName());

        if(isCategoryExist) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();

        Optional<Category> parentCategory = categoryRepository.findById(
                categoryRequest.getParentId());

        parentCategory.ifPresent(category::setParent);

        return categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository
                .findAllCategoriesWithDistinctSubcategories();

        if(categories.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        return categories.stream()
                .map(CategoryResponse::fromCategory)
                .toList();
    }

    @Override
    public CategoryResponse getCategory(long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        return CategoryResponse.fromCategory(category);
    }

    @Override
    @Transactional
    public Category updateCategory(
            long categoryId,
            CategoryRequest categoryRequest) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        if (categoryRequest.getName() != null && !categoryRequest.getName().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByName(categoryRequest.getName())) {
                throw new AppException(ErrorCode.CATEGORY_EXISTED);
            }
            category.setName(categoryRequest.getName());
        }

        if (categoryRequest.getParentId() > 0 &&
                (category.getParent() == null || categoryRequest.getParentId() != category.getParent().getId())) {

            Category parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_CATE_NOT_EXISTED));

            // Checking must not assign itself as a parent category
            if (parentCategory.getId() == categoryId) {
                throw new AppException(ErrorCode.CATEGORY_EXISTED);
            }
            category.setParent(parentCategory);
        }

        // update category
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        if(category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_HAS_SUBCATEGORIES);
        }

        boolean existProducts = productRepository.existsByCategoryId(categoryId);

        if(existProducts) {
            throw new AppException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }

        categoryRepository.delete(category);
    }

}