package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Permission;
import com.thuan.shop_backend.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId")
    List<Permission> findPermissionByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
    void deleteAllByRoleId(@Param("roleId") Long roleId);
}