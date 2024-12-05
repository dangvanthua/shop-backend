package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId")
    List<ProductImage> findByProductId(@Param("productId") long productId);

    @Query("SELECT COUNT(pi) FROM ProductImage pi " +
            "WHERE pi.product.id = :productId AND pi.isThumbnail = true")
    long countByProductIdAndIsThumbnail(@Param("productId") long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id IN :productIds")
    List<ProductImage> findByProductIds(@Param("productIds") List<Long> productIds);
}
