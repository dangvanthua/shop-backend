package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
}
