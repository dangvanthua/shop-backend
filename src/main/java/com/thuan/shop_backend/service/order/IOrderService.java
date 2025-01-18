package com.thuan.shop_backend.service.order;

import com.thuan.shop_backend.dto.request.order.OrderFilterRequest;
import com.thuan.shop_backend.dto.request.order.OrderRequest;
import com.thuan.shop_backend.dto.request.order.OrderStatusRequest;
import com.thuan.shop_backend.dto.response.order.OrderHistoryResponse;
import com.thuan.shop_backend.entity.Order;
import org.springframework.data.domain.Pageable;


public interface IOrderService {
    String createOrder(OrderRequest orderRequest);
    Order updateOrderStatus(long orderId, OrderStatusRequest orderStatusRequest);
    OrderHistoryResponse getOrderByFilterAndPaginate(OrderFilterRequest orderFilterRequest, Pageable pageable);
}
