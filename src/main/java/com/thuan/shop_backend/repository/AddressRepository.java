package com.thuan.shop_backend.repository;

import com.thuan.shop_backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a " +
            "JOIN a.user u " +
            "WHERE u.id = :userId " +
            "AND a.id = :addressId")
    Optional<Address> findByUserIdAndAddressId(
            @Param("userId") long userId,
            @Param("addressId") long addressId);

    @Query("SELECT a FROM Address a " +
            "JOIN a.user u " +
            "ON u.id = :userId")
    List<Address> findByUserId(@Param("userId") long userId);
}
