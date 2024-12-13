package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByName(String name);
}
