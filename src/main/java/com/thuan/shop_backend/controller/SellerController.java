package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.seller.SellerRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.seller.SellerResponse;
import com.thuan.shop_backend.entity.Seller;
import com.thuan.shop_backend.service.seller.ISellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final ISellerService sellerService;

    @PostMapping
    public ApiResponse<Seller> createSeller(
            @Valid @RequestBody SellerRequest sellerRequest) {
        Seller seller = sellerService.createSeller(sellerRequest);
        return ApiResponse.<Seller>builder()
                .message("Create seller success")
                .result(seller)
                .build();
    }

    @GetMapping
    public ApiResponse<List<SellerResponse>> getAllSellers() {
        List<SellerResponse> sellerResponses = sellerService.getAllSellers();
        return ApiResponse.<List<SellerResponse>>builder()
                .message("Get all sellers success")
                .result(sellerResponses)
                .build();
    }

    @PutMapping
    public ApiResponse<Seller> updateSeller(
            @Valid @RequestBody SellerRequest sellerRequest) {
        Seller seller = sellerService.updateSeller(sellerRequest);
        return ApiResponse.<Seller>builder()
                .message("Update seller success")
                .result(seller)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SellerResponse> getSeller(@PathVariable("id") long sellerId) {
        SellerResponse sellerResponse = sellerService.getSeller(sellerId);
        return ApiResponse.<SellerResponse>builder()
                .message("Get seller success")
                .result(sellerResponse)
                .build();
    }
}