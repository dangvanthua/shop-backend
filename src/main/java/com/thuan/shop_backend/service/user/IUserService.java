package com.thuan.shop_backend.service.user;

import com.thuan.shop_backend.dto.request.UserCreateRequest;
import com.thuan.shop_backend.dto.response.UserResponse;

public interface IUserService {
    UserResponse createUser(UserCreateRequest userCreateRequest);
}
