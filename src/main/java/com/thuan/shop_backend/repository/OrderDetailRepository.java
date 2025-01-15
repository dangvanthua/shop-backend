package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT od FROM OrderDetail od WHERE od.order.id = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") long orderId);
}
