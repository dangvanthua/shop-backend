package com.thuan.shop_backend.service.seller;

import com.thuan.shop_backend.constant.PredefinedRole;
import com.thuan.shop_backend.dto.request.SellerRequest;
import com.thuan.shop_backend.dto.response.SellerResponse;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.RoleRepository;
import com.thuan.shop_backend.repository.SellerRepository;
import com.thuan.shop_backend.repository.UserRepository;
import com.thuan.shop_backend.repository.UserRoleRepository;
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


    @Override
    @Transactional
    public SellerResponse createSeller(SellerRequest sellerRequest) {

        User user = userRepository.findById(sellerRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (sellerRepository.existsByUserId(user.getId())) {
            throw new AppException(ErrorCode.SELLER_ALREADY_EXISTS);
        }

        if (sellerRepository.existsByStoreName(sellerRequest.getStoreName())) {
            throw new AppException(ErrorCode.STORE_NAME_ALREADY_EXISTS);
        }

        Role sellerRole = roleRepository.findByName("SELLER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        if (hasRole(user, PredefinedRole.SELLER.name())) {
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
                .build();

        seller = sellerRepository.save(seller);

        return SellerResponse.fromSeller(seller);
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
}
