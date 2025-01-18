package com.thuan.shop_backend.service.order;

import com.thuan.shop_backend.component.AuthComponent;
import com.thuan.shop_backend.constant.DiscountType;
import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.constant.PaymentMethod;
import com.thuan.shop_backend.constant.ShippingMethod;
import com.thuan.shop_backend.dto.request.cart.CartItemRequest;
import com.thuan.shop_backend.dto.request.email.MailRequest;
import com.thuan.shop_backend.dto.request.order.OrderFilterRequest;
import com.thuan.shop_backend.dto.request.order.OrderRequest;
import com.thuan.shop_backend.dto.request.order.OrderStatusRequest;
import com.thuan.shop_backend.dto.request.payment.PaymentRequest;
import com.thuan.shop_backend.dto.request.payment.PaypalPaymentRequest;
import com.thuan.shop_backend.dto.request.product.ProductRequest;
import com.thuan.shop_backend.dto.response.order.OrderHistoryResponse;
import com.thuan.shop_backend.dto.response.order.OrderResponse;
import com.thuan.shop_backend.entity.*;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.*;
import com.thuan.shop_backend.service.cart.ICartService;
import com.thuan.shop_backend.service.email.IEmailService;
import com.thuan.shop_backend.service.payment.IPaymentService;
import com.thuan.shop_backend.service.product.IProductService;
import com.thuan.shop_backend.service.user.IUserService;
import com.thuan.shop_backend.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

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
    private final IEmailService emailService;
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

        // implement generate tracking number by algorithm
        String trackingNumber = generateTrackingNumber(user.getId());
        order.setTrackingNumber(trackingNumber);

        // Lưu đơn hàng
        order = orderRepository.save(order);

        BigDecimal shippingFee = calculateFeeShip(orderRequest);
        BigDecimal totalPrice = BigDecimal.ZERO;

        // create variable type OrderDetail to save data
        List<OrderDetail> tmpOrderDetails = new ArrayList<>();

        for (CartItemRequest cartItem : orderRequest.getCartItems()) {

            Product product = productRepository
                    .findByProductIdWithSeller(cartItem.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            // check if user is seller then don't create order
            if(Objects.equals(product.getSeller().getUser().getId(), user.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }

            BigDecimal itemTotalMoney = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

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
                    BigDecimal discount = BigDecimal.valueOf(promotionCode.getDiscountValue());
                    if (promotionCode.getPromotion().getDiscountType()
                            .equalsIgnoreCase(DiscountType.PERCENTAGE.name())) {
                        discount = itemTotalMoney.multiply(discount.divide(BigDecimal.valueOf(100)));
                    }

                    // Ensure discount does not exceed item price
                    itemTotalMoney = itemTotalMoney.subtract(discount).max(BigDecimal.ZERO);
                }
            }

            itemTotalMoney = itemTotalMoney.add(shippingFee);
            totalPrice = totalPrice.add(itemTotalMoney);

            // Tạo chi tiết đơn hàng
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .price(BigDecimal.valueOf(product.getPrice()).doubleValue())
                    .numberOfProducts(cartItem.getQuantity())
                    .totalMoney(itemTotalMoney.doubleValue())
                    .build();

            // Lưu chi tiết đơn hàng
            orderDetail = orderDetailRepository.save(orderDetail);
            tmpOrderDetails.add(orderDetail);

            // Xóa sản phẩm trong giỏ hàng
            cartService.removeCartItem(product.getId());

            // Cập nhật lại số lượng sản phẩm
            ProductRequest productRequest = ProductRequest.builder()
                    .quantity(product.getQuantity() - cartItem.getQuantity())
                    .build();

            productService.updateProduct(product.getId(), productRequest);
        }

        // implement builder email request to send email to user
        MailRequest mailRequest = MailRequest.builder()
                .mailFrom("smart-shop@gmail.com")
                .mailTo(user.getEmail())
                .mailSubject("Xác nhận đơn hàng #" + order.getTrackingNumber())
                .mailContent(EmailUtils.buildOrderEmailContent(order, tmpOrderDetails))
                .build();

        if(orderRequest.getPaymentMethod() != null &&
                !orderRequest.getPaymentMethod().equalsIgnoreCase(PaymentMethod.COD.name())) {
            // execute create payment and return payment response
            Map<String, Object> paymentResponse = executeCreatePayment(
                    orderRequest, totalPrice, order.getId());

            // implement save payment table when user pay success
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .orderId(order.getId())
                    .paymentAmount(totalPrice.doubleValue())
                    .build();

            paymentService.savePayment(paymentRequest);

            // implement call to email service when pay type e_wallet
            emailService.sendMailConfirmationOrder(mailRequest);

            return (String) paymentResponse.get("approval_url");
        }

        // implement call to email service when pay type cod
        emailService.sendMailConfirmationOrder(mailRequest);

        return null;
    }

    private String generateTrackingNumber(Long id) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        int randomStringLength = 6;
        String randomString = generateRandomString(randomStringLength);
        String uniqueData = String.format("%s-%s-%d", timestamp, randomString, id);
        String hash = UUID.nameUUIDFromBytes(uniqueData.getBytes()).toString();
        // return tracking number
        return String.format("TRK-%s", hash.substring(0, 12)).toUpperCase();
    }

    private String generateRandomString(int randomStringLength) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < randomStringLength; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    private Map<String, Object> executeCreatePayment(
            OrderRequest orderRequest,
            BigDecimal totalPrice,
            long orderId) {
        if(orderRequest.getPaymentMethod().equalsIgnoreCase(PaymentMethod.E_WALLET.name())) {

            BigDecimal exchangeRate = BigDecimal.valueOf(25000);
            BigDecimal convertedAmount = totalPrice.divide(exchangeRate, 2, RoundingMode.HALF_UP);

            PaypalPaymentRequest payPalPaymentRequest = PaypalPaymentRequest.builder()
                    .total(convertedAmount.doubleValue())
                    .currency("USD")
                    .description(orderRequest.getNote())
                    .cancelUrl("http://localhost:4200/payment/cancel")
                    .successUrl("http://localhost:4200/payment/success/" + orderId)
                    .method("paypal")
                    .intent("sale")
                    .build();

            return paymentService.createPayment(payPalPaymentRequest);
        }

        return null;
    }

    private BigDecimal calculateFeeShip(OrderRequest orderRequest) {
        BigDecimal shippingFee = orderRequest.getShippingMethod().equalsIgnoreCase(ShippingMethod.EXPRESS.name())
                ? BigDecimal.valueOf(30000) : BigDecimal.valueOf(10000);
        int totalQuantity = orderRequest.getCartItems().size();
        return shippingFee.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SELLER')")
    public Order updateOrderStatus(long orderId, OrderStatusRequest orderStatusRequest) {
        // Lay thong tin cua user
        String email = authComponent.getEmailFromAuthentication();

        User user = userService.getUserByEmail(email);

        // Lấy thông tin đơn hàng
        Order order = orderRepository.findByIdWithUser(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));


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

    @Override
    public OrderHistoryResponse getOrderByFilterAndPaginate(
            OrderFilterRequest orderFilterRequest,
            Pageable pageable) {

        // Lấy thông tin của user
        String email = authComponent.getEmailFromAuthentication();
        User user = userService.getUserByEmail(email);
        Long userId = user.getId();

        OrderStatus orderStatus = null;
        String reference = null;

        if(orderFilterRequest.getStatus() != null) {
            orderStatus = OrderStatus.valueOf(orderFilterRequest.getStatus().toUpperCase());
        }

        if(orderFilterRequest.getReference() != null) {
            reference = orderFilterRequest.getReference().trim();
        }

        Page<Order> orderPage = orderRepository.findOrderByUserId(
                orderStatus,
                reference,
                userId,
                pageable);

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(OrderResponse::fromOrder)
                .toList();

        return OrderHistoryResponse.builder()
                .orderResponses(orderResponses)
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();
    }
}
