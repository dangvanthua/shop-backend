package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {
    @Query("""
    SELECT va FROM VariantAttribute va 
    JOIN FETCH va.productVariant pv 
    JOIN FETCH va.attribute 
    WHERE pv.id = :productVariantId
    """)
    List<VariantAttribute> findAllVariantAttByProductId(
            @Param("productVariantId") long productVariantId);
}