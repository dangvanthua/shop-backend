package com.thuan.shop_backend.utils;

import com.thuan.shop_backend.entity.Order;
import com.thuan.shop_backend.entity.OrderDetail;

import java.util.List;

public class EmailUtils {

    public static String buildOrderEmailContent(Order order, List<OrderDetail> orderDetails) {
        double totalPrice = 0.0;
        StringBuilder content = new StringBuilder();

        content.append("Chào ").append(order.getUser().getFullName()).append(",\n\n");
        content.append("Cảm ơn bạn đã đặt hàng tại Shop của chúng tôi! Đây là thông tin đơn hàng của bạn:\n\n");
        content.append("Mã đơn hàng: ").append(order.getTrackingNumber()).append("\n");
        content.append("Ngày đặt hàng: ").append(order.getOrderDate()).append("\n");
        content.append("Phương thức thanh toán: ").append(order.getPaymentMethod()).append("\n\n");
        content.append("Chi tiết sản phẩm:\n");

        for (OrderDetail detail : orderDetails) {
            content.append("- Sản phẩm: ").append(detail.getProduct().getName())
                    .append(", Số lượng: ").append(detail.getNumberOfProducts())
                    .append(", Giá: ").append(detail.getPrice()).append(" VND\n");
            totalPrice += detail.getTotalMoney();
        }

        content.append("\nTổng cộng: ").append(totalPrice).append(" VND\n");
        content.append("Địa chỉ giao hàng: ").append(order.getShippingAddress()).append("\n\n");
        content.append("Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ chúng tôi qua email hoặc số điện thoại hỗ trợ.\n");
        content.append("Cảm ơn bạn đã tin tưởng sử dụng dịch vụ của chúng tôi!\n\n");
        content.append("Trân trọng,\n");
        content.append("Đội ngũ Shop");
        return content.toString();
    }

}
