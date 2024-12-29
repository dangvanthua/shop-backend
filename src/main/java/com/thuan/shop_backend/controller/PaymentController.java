package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.payment.VerifyPaymentRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.service.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/execute")
    public ApiResponse<Map<String, Object>> executePayment(
            @RequestBody VerifyPaymentRequest verifyPaymentRequest) {

        Map<String, Object> paymentResponse = paymentService.executePayment(verifyPaymentRequest);
        return ApiResponse.<Map<String, Object>>builder()
                .message("Execute payment success")
                .result(paymentResponse)
                .build();
    }
}
