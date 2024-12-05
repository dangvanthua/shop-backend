package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") long categoryId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.id = :categoryId")
    boolean existsProductsInCategory(@Param("categoryId") long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    Page<Product> findProductsByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.category.id = :categoryId")
    Page<Product> findProductsByCategoryId(
            @Param("categoryId") long categoryId,
            Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderDetail od ON od.product.id = p.id " +
            "JOIN od.order o " +
            "WHERE o.status = :status AND o.active = true " +
            "GROUP BY p.id " +
            "ORDER BY SUM(od.numberOfProducts) DESC")
    Page<Product> findProductsByTopSelling(
            @Param("status") OrderStatus status,
            Pageable pageable);
}