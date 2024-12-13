package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.ProductPromotionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductPromotionRepository extends JpaRepository<ProductPromotionCode, Long> {
    @Query("SELECT ppc FROM ProductPromotionCode ppc " +
            "JOIN FETCH ppc.promotionCode pc " +
            "JOIN pc.promotion pr " +
            "WHERE ppc.product.id = :productId " +
            "AND pr.isActive = true " +
            "AND pc.isActive = true")
    List<ProductPromotionCode> findByProductId(@Param("productId") long productId);

    @Query("SELECT ppc FROM ProductPromotionCode ppc " +
            "WHERE ppc.product.id = :productId " +
            "AND ppc.promotionCode.id = :promotionId")
    Optional<ProductPromotionCode> findByProductIdAndPromotionId(
            @Param("productId") long productId,
            @Param("promotionId") long promotionId);
}
