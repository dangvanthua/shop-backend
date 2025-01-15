package com.thuan.shop_backend.dto.request.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String mailFrom;
    private String mailTo;
    private String mailCc;
    private String mailBcc;
    private String mailSubject;
    private String mailContent;
    private String contentType;
    private List<Object> attachments;
}
