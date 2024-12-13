package com.thuan.shop_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "product_promotion_codes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPromotionCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_code_id", nullable = false)
    private PromotionCode promotionCode;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDate createdAt;
}
