package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.cart.CartRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.cart.CartResponse;
import com.thuan.shop_backend.entity.CartItem;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @PostMapping
    public ApiResponse<Void> createCartItem(@RequestBody CartRequest cartRequest) {
        CartItem cartItem = cartService.createCartItem(cartRequest);
        return ApiResponse.<Void>builder()
                .message("Create cart item success")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateCartItem(
            @PathVariable("id") long cartId,
            @RequestBody CartRequest cartRequest) {
        CartItem cartItem = cartService.updateCartItem(cartId, cartRequest);
        return ApiResponse.<Void>builder()
                .message("Update new quantity of cart success")
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteCartItems(@RequestBody List<Long> productIds) {
        if(productIds == null || productIds.isEmpty()) {
            throw new AppException(ErrorCode.CART_NOT_EXISTED);
        }
        cartService.removeCartItems(productIds);
        return ApiResponse.<Void>builder()
                .message("Delete cart items success")
                .build();
    }

    @GetMapping
    public ApiResponse<List<CartResponse>> getAllCarts() {
        List<CartResponse> cartResponses = cartService.getAllCartItems();
        return ApiResponse.<List<CartResponse>>builder()
                .message("Get all cart items success")
                .result(cartResponses)
                .build();
    }
}
