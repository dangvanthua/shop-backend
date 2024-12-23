package com.thuan.shop_backend.service.payment.paypal;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.thuan.shop_backend.service.payment.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaypalPaymentService implements IPaymentService {

    private final APIContext apiContext;



}
