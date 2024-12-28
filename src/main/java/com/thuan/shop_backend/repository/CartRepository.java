package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c " +
            "JOIN c.product p " +
            "JOIN c.user u " +
            "WHERE p.id = :productId " +
            "AND u.email = :email")
    Optional<CartItem> findByProductIdAndEmailUser(
            @Param("productId") long productId,
            @Param("email") String email);

    @Query("SELECT c FROM CartItem c " +
            "JOIN c.user u " +
            "WHERE u.id = :userId")
    List<CartItem> findByUserId(@Param("userId") long userId);

    @Query("SELECT c FROM CartItem c " +
            "JOIN c.product p " +
            "JOIN c.user u " +
            "WHERE p.id IN :productIds " +
            "AND u.id = :userId")
    List<CartItem> findByUserIdAndProductIds(
            @Param("userId") long userId,
            @Param("productIds") List<Long> productIds);
}
