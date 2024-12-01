package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.SellerRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.SellerResponse;
import com.thuan.shop_backend.service.seller.ISellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final ISellerService sellerService;

    @PostMapping
    public ApiResponse<SellerResponse> createSeller(@Valid SellerRequest sellerRequest) {
        SellerResponse sellerResponse = sellerService.createSeller(sellerRequest);
        return ApiResponse.<SellerResponse>builder()
                .message("Create seller success")
                .result(sellerResponse)
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

}