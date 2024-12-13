package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.order.OrderRequest;
import com.thuan.shop_backend.dto.request.order.OrderStatusRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.entity.Order;
import com.thuan.shop_backend.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ApiResponse<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        return ApiResponse.<Order>builder()
                .message("Create order success")
                .result(order)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<Order> updateOrderStatus(
            @PathVariable("id") long orderId,
            @RequestBody OrderStatusRequest orderStatusRequest) {
        Order order = orderService.updateOrderStatus(orderId, orderStatusRequest);
        return ApiResponse.<Order>builder()
                .message("Order status update success")
                .result(order)
                .build();
    }
}
