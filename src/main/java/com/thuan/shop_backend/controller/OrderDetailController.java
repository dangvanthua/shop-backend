package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.order.OrderDetailResponse;
import com.thuan.shop_backend.service.order_detail.IOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService orderDetailService;

    @GetMapping("/{id}")
    public ApiResponse<List<OrderDetailResponse>> getOrderDetailsByOrderId(
            @PathVariable("id") long orderId) {
        List<OrderDetailResponse> orderDetailResponses = orderDetailService
                .getOrderDetailsByOrderId(orderId);
        return ApiResponse.<List<OrderDetailResponse>>builder()
                .message("Get order detail response success")
                .result(orderDetailResponses)
                .build();
    }
}
