package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :orderId")
    Optional<Order> findByIdWithUser(@Param("orderId") long orderId);
}
