package com.thuan.shop_backend.service.order_detail;

import com.thuan.shop_backend.dto.response.order.OrderDetailResponse;
import com.thuan.shop_backend.entity.OrderDetail;
import com.thuan.shop_backend.entity.ProductImage;
import com.thuan.shop_backend.repository.OrderDetailRepository;
import com.thuan.shop_backend.service.product_image.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService{

    private final OrderDetailRepository orderDetailRepository;
    private final ProductImageService productImageService;

    @Override
    public List<OrderDetailResponse> getOrderDetailsByOrderId(long orderId) {

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);

        List<Long> productIds = orderDetails.stream()
                .map(orderDetail ->
                        orderDetail.getProduct().getId())
                .toList();

        List<ProductImage> productImages = productImageService
                .getByProductIds(productIds);

        return orderDetails.stream()
                .map(orderDetail -> OrderDetailResponse.fromOderDetail(
                        orderDetail,
                        productImages.stream()
                                .filter(image ->
                                        image.getProduct().getId()
                                                .equals(orderDetail.getProduct().getId()))
                                .toList()))
                .toList();
    }
}
