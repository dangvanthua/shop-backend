package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Boolean existsByName(String name);
}
