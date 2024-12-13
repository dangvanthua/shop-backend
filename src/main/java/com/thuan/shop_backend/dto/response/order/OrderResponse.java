package com.thuan.shop_backend.dto.response.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    @JsonProperty("id")
    private long id;

    @JsonProperty("note")
    private String note;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("status")
    private String status;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("order_details")
    private List<OrderDetailResponse> orderDetailResponses;

    public static OrderResponse fromOrder(
            Order order,
            List<OrderDetailResponse> orderDetailResponses) {

        return OrderResponse.builder()
                .id(order.getId())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .shippingAddress(order.getShippingAddress())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus().name())
                .trackingNumber(order.getTrackingNumber())
                .orderDetailResponses(orderDetailResponses)
                .build();
    }
}
