package com.thuan.shop_backend.service.email;

import com.thuan.shop_backend.dto.request.email.MailRequest;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmailService implements IEmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @Async("taskExecutor")
    public void sendMailConfirmationOrder(MailRequest emailRequest) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailRequest.getMailFrom());
            mailMessage.setTo(emailRequest.getMailTo());
            mailMessage.setSubject(emailRequest.getMailSubject());
            mailMessage.setText(emailRequest.getMailContent());
            mailMessage.setSentDate(new Date());

            javaMailSender.send(mailMessage);
        }catch (Exception ex) {
            throw new AppException(ErrorCode.SEND_MAIL_FAILED);
        }
    }
}
