package com.thuan.shop_backend.service.order;

import com.thuan.shop_backend.dto.request.order.OrderRequest;
import com.thuan.shop_backend.dto.request.order.OrderStatusRequest;
import com.thuan.shop_backend.entity.Order;

public interface IOrderService {
    Order createOrder(OrderRequest orderRequest);
    Order updateOrderStatus(long orderId, OrderStatusRequest orderStatusRequest);
}
