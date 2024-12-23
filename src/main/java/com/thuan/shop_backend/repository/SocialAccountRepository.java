package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    boolean existsByProviderId(String providerId);
}
