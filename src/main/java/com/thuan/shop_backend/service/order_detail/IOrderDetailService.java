package com.thuan.shop_backend.service.order_detail;

import com.thuan.shop_backend.dto.response.order.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {
    List<OrderDetailResponse> getOrderDetailsByOrderId(long orderId);
}
