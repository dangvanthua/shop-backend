package com.thuan.shop_backend.service.order;

import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.constant.DiscountType;
import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.constant.PaymentMethod;
import com.thuan.shop_backend.constant.ShippingMethod;
import com.thuan.shop_backend.dto.request.cart.CartItemRequest;
import com.thuan.shop_backend.dto.request.order.OrderRequest;
import com.thuan.shop_backend.dto.request.order.OrderStatusRequest;
import com.thuan.shop_backend.dto.request.payment.BasePaymentRequest;
import com.thuan.shop_backend.dto.request.payment.PaypalPaymentRequest;
import com.thuan.shop_backend.dto.request.product.ProductRequest;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import com.thuan.shop_backend.service.cart.ICartService;
import com.thuan.shop_backend.service.payment.IPaymentService;
import com.thuan.shop_backend.service.product.IProductService;
import com.thuan.shop_backend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PromotionCodeRepository promotionCodeRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final ProductRepository productRepository;
    private final AuthComponent authComponent;
    private final ModelMapper mapper;

    private final IProductService productService;
    private final IUserService userService;
    private final ICartService cartService;
    private final IPaymentService paymentService;

    @Override
    @Transactional
    public String createOrder(OrderRequest orderRequest) {

        String email = authComponent.getEmailFromAuthentication();

        User user = userService.getUserByEmail(email);

        Order order = mapper.map(orderRequest, Order.class);
        order.setPaymentMethod(orderRequest.getPaymentMethod().toLowerCase());
        order.setShippingMethod(orderRequest.getShippingMethod().toLowerCase());
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);

        // Lưu đơn hàng
        order = orderRepository.save(order);

        double shippingFee = calculateFeeShip(orderRequest);
        double totalPrice = 0.0;

        for (CartItemRequest cartItem : orderRequest.getCartItems()) {

            Product product = productRepository
                    .findById(cartItem.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            double itemTotalMoney = product.getPrice() * cartItem.getQuantity();

            // Áp dụng từng mã giảm giá
            if (cartItem.getPromotionCode() != null && !cartItem.getPromotionCode().isEmpty()) {
                for (String promoCode : cartItem.getPromotionCode()) {
                    PromotionCode promotionCode = promotionCodeRepository.findByCode(promoCode)
                            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_CODE_NOT_EXISTED));

                    if (!promotionCode.getIsActive()) {
                        throw new AppException(ErrorCode.PROMOTION_CODE_EXPIRED);
                    }

                    if (promotionCode.getEndDate().isBefore(LocalDate.now())) {
                        throw new AppException(ErrorCode.PROMOTION_CODE_EXPIRED);
                    }

                    ProductPromotionCode productPromotionCode = productPromotionRepository
                            .findByProductIdAndPromotionId(product.getId(), promotionCode.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_CODE_EXPIRED));

                    // Tính giảm giá
                    double discount = promotionCode.getDiscountValue();
                    if (promotionCode.getPromotion().getDiscountType()
                            .equalsIgnoreCase(DiscountType.PERCENTAGE.name())) {
                        discount = itemTotalMoney * (promotionCode.getDiscountValue() / 100);
                    }

                    // Trừ giảm giá (đảm bảo không âm)
                    itemTotalMoney -= Math.min(discount, itemTotalMoney);
                }
            }

            itemTotalMoney += shippingFee;
            totalPrice += itemTotalMoney;
            // Tạo chi tiết đơn hàng
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .price(product.getPrice())
                    .numberOfProducts(cartItem.getQuantity())
                    .totalMoney(itemTotalMoney)
                    .build();

            // Lưu chi tiết đơn hàng
            orderDetailRepository.save(orderDetail);

            // Xóa sản phẩm trong giỏ hàng
            cartService.removeCartItem(product.getId());

            // Cập nhật lại số lượng sản phẩm
            ProductRequest productRequest = ProductRequest.builder()
                    .quantity(product.getQuantity() - cartItem.getQuantity())
                    .build();

            productService.updateProduct(product.getId(), productRequest);
        }

        if(orderRequest.getPaymentMethod().equalsIgnoreCase(PaymentMethod.E_WALLET.name())) {
            PaypalPaymentRequest paymentRequest = PaypalPaymentRequest.builder()
                    .total(totalPrice / 23000)
                    .currency("USD")
                    .description(orderRequest.getNote())
                    .cancelUrl("http://localhost:4200/payment/cancel")
                    .successUrl("http://localhost:4200/payment/success")
                    .method("paypal")
                    .intent("sale")
                    .build();

            Map<String, Object> paymentResponse = paymentService.createPayment(paymentRequest);

            return (String) paymentResponse.get("approval_url");
        }

        return null;
    }

    private double calculateFeeShip(OrderRequest orderRequest) {
        double shippingFee = orderRequest.getShippingMethod().equalsIgnoreCase(ShippingMethod.EXPRESS.name())
                ? 30000 : 10000;
        int totalQuantity = orderRequest.getCartItems().size();

        return (shippingFee / totalQuantity);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SELLER')")
    public Order updateOrderStatus(long orderId, OrderStatusRequest orderStatusRequest) {
        // Lấy thông tin đơn hàng
        Order order = orderRepository.findByIdWithUser(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        // Lay thong tin cua user
        String email = authComponent.getEmailFromAuthentication();

        User user = userService.getUserByEmail(email);

        if(!Objects.equals(order.getUser().getId(), user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        OrderStatus newStatus;

        try {
            newStatus = OrderStatus.valueOf(orderStatusRequest.getOrderStatus());
        }catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        if(!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // Cập nhật trạng thái
        order.setStatus(newStatus);
        order.setShippingDate(LocalDate.now());

        return orderRepository.save(order);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
