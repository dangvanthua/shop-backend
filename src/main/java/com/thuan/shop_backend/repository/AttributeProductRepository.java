package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Attribute;
import com.thuan.shop_backend.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeProductRepository extends JpaRepository<ProductAttribute, Long> {
    List<ProductAttribute> findByAttribute(Attribute attribute);
}
