package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.PaymentStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoRepository extends JpaRepository<PaymentStore, Long> {
}
