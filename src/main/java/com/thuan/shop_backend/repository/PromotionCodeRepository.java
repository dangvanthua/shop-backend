package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.PromotionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionCodeRepository extends JpaRepository<PromotionCode, Long> {
    Optional<PromotionCode> findByCode(String code);
}
