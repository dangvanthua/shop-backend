package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    List<Category> findByParentIsNull();

    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentCategoryId")
    List<Category> findSubcategories(@Param("parentCategoryId") long parentCategoryId);

}
