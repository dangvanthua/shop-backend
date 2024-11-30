package com.thuan.shop_backend.service.category;

import com.thuan.shop_backend.dto.request.CategoryRequest;
import com.thuan.shop_backend.dto.response.CategoryResponse;
import com.thuan.shop_backend.entity.Attribute;
import com.thuan.shop_backend.entity.Category;
import com.thuan.shop_backend.entity.CategoryAttribute;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import com.thuan.shop_backend.service.attribute.IAttributeService;
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
    private final AttributeCateRepository attCateRepository;
    private final IAttributeService attributeService;

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

        List<Attribute> attributes = attributeService.getAttributeById(categoryRequest.getAttributeIds());

        for (Attribute attribute : attributes) {
            CategoryAttribute categoryAttribute = CategoryAttribute.builder()
                    .category(category)
                    .attribute(attribute)
                    .build();

            attCateRepository.save(categoryAttribute);
        }

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

    @Override
    @Transactional
    public CategoryResponse updateCategory(
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

        if (categoryRequest.getAttributeIds() != null) {
            List<Long> newAttributeIds = categoryRequest.getAttributeIds();
            List<CategoryAttribute> existingAttributes = attCateRepository.findByCategory(category);

            // Get current list attribute id
            List<Long> existingAttributeIds = existingAttributes.stream()
                    .map(ca -> ca.getAttribute().getId())
                    .toList();

            // remove attributes don't contain new list
            List<CategoryAttribute> attributesToDelete = existingAttributes.stream()
                    .filter(ca -> !newAttributeIds.contains(ca.getAttribute().getId()))
                    .toList();
            attCateRepository.deleteAll(attributesToDelete);

            // add to new attributes
            List<Long> attributesToAdd = newAttributeIds.stream()
                    .filter(attrId -> !existingAttributeIds.contains(attrId))
                    .toList();

            List<Attribute> attributes = attributeService.getAttributeById(attributesToAdd);

            for (Attribute attribute : attributes) {
                CategoryAttribute categoryAttribute = CategoryAttribute.builder()
                        .category(category)
                        .attribute(attribute)
                        .build();

                attCateRepository.save(categoryAttribute);
            }
        }

        // update category
        category = categoryRepository.save(category);

        // return category response
        return CategoryResponse.fromCategory(category);
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

        List<CategoryAttribute> categoryAttributes = attCateRepository.findByCategory(category);
        if (!categoryAttributes.isEmpty()) {
            attCateRepository.deleteAll(categoryAttributes);
        }

        categoryRepository.delete(category);
    }
}