package com.thuan.shop_backend.dto.request.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.constant.PaymentMethod;
import com.thuan.shop_backend.constant.ShippingMethod;
import com.thuan.shop_backend.dto.request.cart.CartItemRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    @JsonProperty("note")
    private String note;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("shipping_method")
    private ShippingMethod shippingMethod;

    @JsonProperty("cart_items")
    private List<CartItemRequest> cartItems;
}
