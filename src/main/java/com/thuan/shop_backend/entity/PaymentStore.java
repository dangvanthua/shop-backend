package com.thuan.shop_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "wallet_provider")
    private String walletProvider;

    @Column(name = "wallet_address")
    private String walletAddress;
}
