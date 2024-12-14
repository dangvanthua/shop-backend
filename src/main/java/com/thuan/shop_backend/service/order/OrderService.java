package com.thuan.shop_backend.service.order;

import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.constant.DiscountType;
import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.dto.request.cart.CartItemRequest;
import com.thuan.shop_backend.dto.request.order.OrderRequest;
import com.thuan.shop_backend.dto.request.order.OrderStatusRequest;
import com.thuan.shop_backend.dto.request.product.ProductRequest;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import com.thuan.shop_backend.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final PromotionCodeRepository promotionCodeRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final ProductRepository productRepository;
    private final AuthComponent authComponent;
    private final ModelMapper mapper;

    private final IProductService productService;

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {

        String email = authComponent.getEmailFromAuthentication();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Order order = mapper.map(orderRequest, Order.class);
        order.setPaymentMethod(orderRequest.getPaymentMethod().name().toLowerCase());
        order.setShippingMethod(orderRequest.getShippingMethod().name().toLowerCase());
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);

        // luu don hang
        order = orderRepository.save(order);

        for(CartItemRequest cartItem : orderRequest.getCartItems()) {

            Product product = productRepository
                    .findById(cartItem.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            double itemTotalMoney = product.getPrice() * cartItem.getQuantity();

            if(cartItem.getPromotionCode() != null) {
                PromotionCode promotionCode = promotionCodeRepository.findByCode(cartItem.getPromotionCode())
                        .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_CODE_NOT_EXISTED));

                if(!promotionCode.getIsActive()) {
                    throw new AppException(ErrorCode.PROMOTION_CODE_NOT_EXISTED);
                }

                if(promotionCode.getEndDate().isBefore(LocalDate.now())) {
                    throw new AppException(ErrorCode.PROMOTION_CODE_EXPIRED);
                }

                ProductPromotionCode productPromotionCode = productPromotionRepository
                        .findByProductIdAndPromotionId(product.getId(), promotionCode.getId())
                        .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_CODE_NOT_EXISTED));

                // ap dung giam gia
                double discount = promotionCode.getDiscountValue();
                if(promotionCode.getPromotion().getDiscountType()
                        .equalsIgnoreCase(DiscountType.PERCENTAGE.name())) {
                    discount = itemTotalMoney * (promotionCode.getDiscountValue() / 100);
                }

                itemTotalMoney -= Math.min(discount, itemTotalMoney);
            }

            // Tao chi tiet don hang
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .price(product.getPrice())
                    .numberOfProducts(cartItem.getQuantity())
                    .totalMoney(itemTotalMoney)
                    .build();

            // luu chi tiet don hang
            orderDetailRepository.save(orderDetail);

            // cap nhat lai so luong cua san pham
            ProductRequest productRequest = ProductRequest.builder()
                    .quantity(product.getQuantity() - cartItem.getQuantity())
                    .build();

            productService.updateProduct(product.getId(), productRequest);
        }

        return order;
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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

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
            default -> false;
        };
    }
}
