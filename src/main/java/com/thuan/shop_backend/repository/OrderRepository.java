package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :orderId")
    Optional<Order> findByIdWithUser(@Param("orderId") long orderId);

    @Query("SELECT o FROM Order o " +
            "WHERE o.user.id = :userId " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:reference IS NULL OR o.trackingNumber LIKE :reference)")
    Page<Order> findOrderByUserId(
            @Param("status") OrderStatus status,
            @Param("reference") String reference,
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("SELECT DISTINCT o.product.seller.id FROM OrderDetail o WHERE o.order.user.id = :userId")
    List<Long> findSellerIdsByUserId(@Param("userId") Long userId);
}