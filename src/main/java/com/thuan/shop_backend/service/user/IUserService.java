package com.thuan.shop_backend.service.user;

import com.thuan.shop_backend.dto.request.user.UserCreateRequest;
import com.thuan.shop_backend.dto.request.user.UserLoginRequest;
import com.thuan.shop_backend.dto.response.user.UserResponse;
import com.thuan.shop_backend.entity.User;

import java.util.List;

public interface IUserService {
    User createUser(UserCreateRequest userCreateRequest);
    User createUserSocialAccount(UserLoginRequest userLoginRequest);
    List<UserResponse> getAllUsers();
    UserResponse getUser(long userId);
    User updateUser(long userId, UserCreateRequest userCreateRequest);
    void deactivateUser(long userId);
    void deleteUser(long userId);
    void uploadAvatarUser(long userId, String publicId, String avatarUrl);
    User getUserByEmail(String email);
    UserResponse getUserDetail();
}
