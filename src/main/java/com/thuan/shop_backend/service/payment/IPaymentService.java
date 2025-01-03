package com.thuan.shop_backend.service.payment;

import com.thuan.shop_backend.dto.request.payment.BasePaymentRequest;
import com.thuan.shop_backend.dto.request.payment.PaymentRequest;
import com.thuan.shop_backend.dto.request.payment.VerifyPaymentRequest;

import java.util.Map;

public interface IPaymentService {
    String getAccessToken();
    Map<String, Object> createPayment(BasePaymentRequest basePaymentRequest);
    Map<String, Object> executePayment(VerifyPaymentRequest verifyPaymentRequest);
    void savePayment(PaymentRequest paymentRequest);
}
