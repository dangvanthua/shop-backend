package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);

    @Query(value = """
        SELECT DISTINCT u.id,  
                        u.fullname,
                        u.phone_number,
                        u.email,
                        u.is_active,
                        u.date_of_birth,
                        u.avatar, 
                        u.created_at,
                        u.updated_at,
                        r.id AS role_id, 
                        r.name AS role_name,
                        r.description AS role_description
        FROM orders o
        JOIN order_details od ON o.id = od.order_id
        JOIN products p ON od.product_id = p.id
        JOIN sellers s ON p.seller_id = s.id
        JOIN users u ON s.user_id = u.id
        JOIN user_roles ur ON ur.user_id = u.id
        JOIN roles r ON ur.role_id = r.id
        WHERE o.user_id = :userId
          AND r.id = :roleId
    """, nativeQuery = true)
    List<Object[]> findSellersForBuyer(
            @Param("userId") Long userId,
            @Param("roleId") Long roleId);
}
