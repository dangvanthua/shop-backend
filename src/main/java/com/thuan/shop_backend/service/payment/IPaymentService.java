package com.thuan.shop_backend.service.payment;

import com.thuan.shop_backend.dto.request.payment.BasePaymentRequest;

import java.util.Map;

public interface IPaymentService {
    String getAccessToken();
    Map<String, Object> createPayment(BasePaymentRequest basePaymentRequest);
    Map<String, Object> executePayment(String paymentId, String payerId);
}
