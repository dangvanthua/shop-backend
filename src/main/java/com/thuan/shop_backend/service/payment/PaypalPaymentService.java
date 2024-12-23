package com.thuan.shop_backend.service.payment;

import com.thuan.shop_backend.dto.request.payment.BasePaymentRequest;
import com.thuan.shop_backend.dto.request.payment.PaypalPaymentRequest;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaypalPaymentService implements IPaymentService {

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

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

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

        if(basePaymentRequest instanceof PaypalPaymentRequest paypalRequest) {

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
                            "return_url", paypalRequest.getCancelUrl(),
                            "cancel_url", basePaymentRequest.getCancelUrl(),
                            "success_url", basePaymentRequest.getSuccessUrl()
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

            return response.getBody();
        }

        throw new AppException(ErrorCode.PAYMENT_FAILED);
    }

    @Override
    public Map<String, Object> executePayment(String paymentId, String payerId) {
        String url = baseUrl + "/v1/payments/payment/" + paymentId + "/execute";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = Map.of("payer_id", payerId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class);

        return response.getBody();
    }
}
