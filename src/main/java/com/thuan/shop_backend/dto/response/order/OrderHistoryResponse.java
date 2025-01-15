package com.thuan.shop_backend.dto.response.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistoryResponse {

    @JsonProperty("order_responses")
    private List<OrderResponse> orderResponses;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("total_elements")
    private long totalElements;
}
