package com.thuan.shop_backend.dto.response;

import com.thuan.shop_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT ")
    Long countReviewsBySellerId(Long sellerId);
}
