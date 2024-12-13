package com.thuan.shop_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thuan.shop_backend.entity.OrderDetail;
import com.thuan.shop_backend.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {

    @JsonProperty("product_response")
    private ProductResponse productResponse;

    @JsonProperty("total_money")
    private double totalMoney;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    public static OrderDetailResponse fromOderDetail(
            OrderDetail orderDetail,
            List<ProductImage> productImages) {

        return OrderDetailResponse.builder()
                .totalMoney(orderDetail.getTotalMoney())
                .numberOfProducts(orderDetail.getNumberOfProducts())
                .productResponse(ProductResponse.fromProduct(
                        orderDetail.getProduct(), productImages))
                .build();
    }
}
