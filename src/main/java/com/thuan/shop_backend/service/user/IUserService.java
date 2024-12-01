package com.thuan.shop_backend.service.user;

import com.thuan.shop_backend.dto.request.UserCreateRequest;
import com.thuan.shop_backend.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse createUser(UserCreateRequest userCreateRequest);
    List<UserResponse> getAllUsers();
    UserResponse getUser(long userId);
    UserResponse updateUser(long userId, UserCreateRequest userCreateRequest);
    void deactivateUser(long userId);
    void deleteUser(long userId);
    void uploadAvatarUser(long userId, String publicId, String avatarUrl);
}
