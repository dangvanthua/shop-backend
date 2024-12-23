package com.thuan.shop_backend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginRequest {
    private String fullName;
    private String email;
    private String picture;
    private String providerId;
    private String providerName;
}
