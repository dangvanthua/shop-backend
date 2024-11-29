package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Attribute;
import com.thuan.shop_backend.entity.Category;
import com.thuan.shop_backend.entity.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttributeCateRepository extends JpaRepository<CategoryAttribute, Long> {
    List<CategoryAttribute> findByAttribute(Attribute attribute);
    List<CategoryAttribute> findByCategory(Category category);
}
