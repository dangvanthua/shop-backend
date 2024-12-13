package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT COUNT(r) FROM Review r " +
            "JOIN r.product p " +
            "WHERE p.seller.id = :sellerId")
    Long countReviewsBySellerId(@Param("sellerId") long sellerId);
}