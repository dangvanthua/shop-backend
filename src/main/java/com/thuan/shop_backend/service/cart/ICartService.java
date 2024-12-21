package com.thuan.shop_backend.service.cart;

import com.thuan.shop_backend.dto.request.cart.CartRequest;
import com.thuan.shop_backend.dto.response.cart.CartResponse;
import com.thuan.shop_backend.entity.CartItem;

import java.util.List;

public interface ICartService {
    CartItem createCartItem(CartRequest cartRequest);
    CartItem updateCartItem(long cartId, CartRequest cartRequest);
    void removeCartItems(List<Long> productIds);
    void removeCartItem(long productId);
    List<CartResponse> getAllCartItems();
}
