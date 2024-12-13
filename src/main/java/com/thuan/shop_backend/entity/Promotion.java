package com.thuan.shop_backend.entity;

import com.thuan.shop_backend.constant.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_value")
    private Float discountValue;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
