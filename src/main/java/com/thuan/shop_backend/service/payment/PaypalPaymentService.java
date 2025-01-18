package com.thuan.shop_backend.service.payment;

import com.thuan.shop_backend.constant.OrderStatus;
import com.thuan.shop_backend.constant.PaymentStatus;
import com.thuan.shop_backend.dto.request.payment.BasePaymentRequest;
import com.thuan.shop_backend.dto.request.payment.PaymentRequest;
import com.thuan.shop_backend.dto.request.payment.PaypalPaymentRequest;
import com.thuan.shop_backend.dto.request.payment.VerifyPaymentRequest;
import com.thuan.shop_backend.entity.Order;
import com.thuan.shop_backend.entity.Payment;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.repository.OrderRepository;
import com.thuan.shop_backend.repository.PaymentRepository;
import com.thuan.shop_backend.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalPaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.api.base-url}")
    private String baseUrl;

    @Override
    public String getAccessToken() {
        String url = baseUrl + "/v1/oauth2/token";
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Sử dụng MultiValueMap thay vì HashMap
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class);

        Map<String, Object> responseBody = response.getBody();

        if(responseBody == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return (String) responseBody.get("access_token");
    }

    @Override
    public Map<String, Object> createPayment(BasePaymentRequest basePaymentRequest) {
        String url = baseUrl + "/v1/payments/payment";
        RestTemplate restTemplate = new RestTemplate();

        if (basePaymentRequest instanceof PaypalPaymentRequest paypalRequest) {

            Map<String, Object> paymentBody = Map.of(
                    "intent", paypalRequest.getIntent(),
                    "payer", Map.of("payment_method", paypalRequest.getMethod()),
                    "transactions", List.of(Map.of(
                            "amount", Map.of(
                                    "total", String.format("%.2f", basePaymentRequest.getTotal()),
                                    "currency", basePaymentRequest.getCurrency()
                            ),
                            "description", basePaymentRequest.getDescription()
                    )),
                    "redirect_urls", Map.of(
                            "return_url", paypalRequest.getSuccessUrl(),  // Địa chỉ trả về sau khi thanh toán thành công
                            "cancel_url", paypalRequest.getCancelUrl()   // Địa chỉ trả về nếu thanh toán bị hủy
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + getAccessToken());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentBody, headers);

            // Gửi yêu cầu tới PayPal
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null) {
                // Trả về URL PayPal mà người dùng có thể truy cập để thanh toán
                List<Map<String, String>> links = (List<Map<String, String>>) responseBody.get("links");
                for (Map<String, String> link : links) {
                    if ("approval_url".equals(link.get("rel"))) {
                        return Map.of("approval_url", link.get("href"));
                    }
                }
            }

            throw new AppException(ErrorCode.PAYMENT_FAILED);
        }

        throw new AppException(ErrorCode.PAYMENT_FAILED);
    }

    @Override
    public Map<String, Object> executePayment(VerifyPaymentRequest verifyPaymentRequest) {
        String url = baseUrl + "/v1/payments/payment/" + verifyPaymentRequest.getPaymentId() + "/execute";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = Map.of("payer_id", verifyPaymentRequest.getPayerId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class);

        // if it is not approved then cancel order
        if(!response.getBody().get("state").equals("approved")) {
            Order order = orderRepository.findById(verifyPaymentRequest.getOrderId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        return response.getBody();
    }

    @Override
    public void savePayment(PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        Payment payment = Payment.builder()
                .order(order)
                .paymentAmount(paymentRequest.getPaymentAmount())
                .paymentStatus(PaymentStatus.PAID)
                .build();
        paymentRepository.save(payment);
    }
}
