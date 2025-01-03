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
            "WHERE pi.product.id = :productId " +
            "AND pi.isThumbnail = :isThumbnail")
    Long countByProductIdAndImage(
            @Param("productId") long productId,
            @Param("isThumbnail") boolean isThumbnail);

    @Query("SELECT pi FROM ProductImage pi " +
            "WHERE pi.product.id IN :productIds " +
            "AND pi.isThumbnail = true")
    List<ProductImage> findByProductIds(@Param("productIds") List<Long> productIds);

    @Query("SELECT pi FROM ProductImage pi " +
            "WHERE pi.product.id = :productId " +
            "AND pi.isThumbnail = :isThumbnail")
    List<ProductImage> findByProductIdAndImage(
            @Param("productId") long productId,
            @Param("isThumbnail") boolean isThumbnail);
}
