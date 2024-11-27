package com.thuan.shop_backend.service.category;

import com.thuan.shop_backend.dto.request.CategoryRequest;
import com.thuan.shop_backend.dto.response.CategoryResponse;
import com.thuan.shop_backend.entity.Category;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        boolean isCategoryExist = categoryRepository.existsByName(categoryRequest.getName());

        if(isCategoryExist) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();

        Optional<Category> parentCategory = categoryRepository.findById(
                categoryRequest.getParentId());

        if(parentCategory.isPresent()) {
            category.setParent(parentCategory.get());
        }

        category = categoryRepository.save(category);

        return CategoryResponse.fromCategory(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();

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
}
