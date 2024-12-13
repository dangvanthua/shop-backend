package com.thuan.shop_backend.service.seller;

import com.thuan.shop_backend.constant.PredefinedRole;
import com.thuan.shop_backend.dto.request.payment.PaymentInfoRequest;
import com.thuan.shop_backend.dto.request.seller.SellerRequest;
import com.thuan.shop_backend.dto.response.seller.SellerResponse;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService implements ISellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PaymentInfoRepository paymentInfoRepository;


    @Override
    @Transactional
    public Seller createSeller(SellerRequest sellerRequest) {

        User user = userRepository.findById(sellerRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (sellerRepository.existsByUserIdOrStoreName(user.getId(), sellerRequest.getStoreName())) {
            throw new AppException(ErrorCode.SELLER_NOT_EXISTED);
        }

        Role sellerRole = roleRepository.findByName(PredefinedRole.SELLER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        if (!hasRole(user, PredefinedRole.SELLER.name())) {
            UserRole userRole = UserRole.builder()
                    .id(new UserRoleId(user.getId(), sellerRole.getId()))
                    .user(user)
                    .role(sellerRole)
                    .build();
            userRoleRepository.save(userRole);
        }

        Seller seller = Seller.builder()
                .storeName(sellerRequest.getStoreName())
                .user(user)
                .isVerified(true)
                .build();

        seller = sellerRepository.save(seller);

        if (sellerRequest.getPaymentInfo() != null) {
            PaymentInfoRequest paymentInfoRequest = sellerRequest.getPaymentInfo();

            // Validate thông tin thanh toán
            if (paymentInfoRequest.getAccountNumber() == null || paymentInfoRequest.getBankName() == null) {
                throw new AppException(ErrorCode.INVALID_PAYMENT_INFO);
            }

            PaymentStore paymentInfo = PaymentStore.builder()
                    .seller(seller)
                    .accountName(paymentInfoRequest.getAccountName())
                    .accountNumber(paymentInfoRequest.getAccountNumber())
                    .bankName(paymentInfoRequest.getBankName())
                    .walletProvider(paymentInfoRequest.getWalletProvider())
                    .walletAddress(paymentInfoRequest.getWalletAddress())
                    .build();
            paymentInfoRepository.save(paymentInfo);
        }

        return seller;
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRole().stream()
                .anyMatch(userRole ->
                        userRole.getRole().getName().equalsIgnoreCase(roleName));
    }

    @Override
    public List<SellerResponse> getAllSellers() {
        List<Seller> sellers = sellerRepository.findAll();
        return sellers.stream()
                .map(SellerResponse::fromSeller)
                .toList();
    }

    @Override
    @Transactional
    public Seller updateSeller(SellerRequest sellerRequest) {

        Seller seller = sellerRepository.findByUserId(sellerRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.SELLER_NOT_EXISTED));

        if (!seller.getStoreName().equalsIgnoreCase(sellerRequest.getStoreName()) &&
                sellerRepository.existsByStoreName(sellerRequest.getStoreName())) {
            throw new AppException(ErrorCode.STORE_NAME_ALREADY_EXISTS);
        }

        seller.setStoreName(sellerRequest.getStoreName());

        return sellerRepository.save(seller);
    }

    @Override
    public SellerResponse getSeller(long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new AppException(ErrorCode.SELLER_NOT_EXISTED));
        return SellerResponse.fromSeller(seller);
    }
}
