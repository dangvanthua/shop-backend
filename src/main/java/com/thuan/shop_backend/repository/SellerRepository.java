package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Seller s WHERE s.user.id = :userId OR s.storeName = :storeName")
    boolean existsByUserIdOrStoreName(@Param("userId") Long userId, @Param("storeName") String storeName);

    @Query("SELECT s FROM Seller s WHERE s.user.id = :userId")
    Optional<Seller> findByUserId(@Param("userId") long userId);

    @Query("SELECT s FROM Seller s JOIN FETCH s.products p WHERE p.id = :productId")
    Optional<Seller> findSellerByProductId(@Param("productId") long productId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.seller.id = :sellerId")
    Long countProductsSoldBySeller(@Param("sellerId") long sellerId);

    boolean existsByStoreName(String storeName);
}
