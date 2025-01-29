package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.dto.request.order.OrderFilterRequest;
import com.thuan.shop_backend.dto.request.order.OrderRequest;
import com.thuan.shop_backend.dto.request.order.OrderStatusRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.order.OrderHistoryResponse;
import com.thuan.shop_backend.entity.Order;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ApiResponse<String> createOrder(@RequestBody OrderRequest orderRequest) {
        String urlOrder = orderService.createOrder(orderRequest);
        return ApiResponse.<String>builder()
                .message("Create order success")
                .result(urlOrder)
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

    @GetMapping
    public ApiResponse<OrderHistoryResponse> getOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reference) {

        Pageable pageable = PageRequest.of(page, size);

        OrderFilterRequest orderFilterRequest = OrderFilterRequest.builder()
                .status(status)
                .reference(reference)
                .build();

        OrderHistoryResponse orderHistoryResponse = orderService
                .getOrderByFilterAndPaginate(orderFilterRequest, pageable);

        return ApiResponse.<OrderHistoryResponse>builder()
                .message("Get order success")
                .result(orderHistoryResponse)
                .build();
    }

    @GetMapping("/{id}/export-pdf")
    public ResponseEntity<byte[]> exportOrderPdf(@PathVariable("id") long orderId) {
        byte[] pdfBytes = orderService.exportOrderPdf(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "order_" + orderId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
