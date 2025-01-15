package com.thuan.shop_backend.service.email;

import com.thuan.shop_backend.dto.request.email.MailRequest;
import com.thuan.shop_backend.entity.Order;
import com.thuan.shop_backend.entity.OrderDetail;

import java.util.List;

public interface IEmailService {
    void sendMailConfirmationOrder(MailRequest emailRequest);
}
