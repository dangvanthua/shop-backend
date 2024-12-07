package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p " +
            "WHERE p.id = :productId")
    List<ProductVariant> findAllProductVariantByProductId(@Param("productId") long productId);

    boolean existsBySku(String sku);
}
