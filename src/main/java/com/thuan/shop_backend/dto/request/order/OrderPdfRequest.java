package com.thuan.shop_backend.dto.request.order;

import com.thuan.shop_backend.entity.Order;
import com.thuan.shop_backend.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPdfRequest {
    private Order order;
    private List<OrderDetail> orderDetail;
}
