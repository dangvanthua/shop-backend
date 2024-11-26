package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.UserCreateRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.UserResponse;
import com.thuan.shop_backend.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest userCreateRequest) {
        UserResponse userResponse = userService.createUser(userCreateRequest);
        return ApiResponse.<UserResponse>builder()
                .message("Create user success")
                .result(userResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers();
        return ApiResponse.<List<UserResponse>>builder()
                .message("Get all user success")
                .result(userResponses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable("id") long userId) {
        UserResponse userResponse = userService.getUser(userId);
        return ApiResponse.<UserResponse>builder()
                .message("Get user success")
                .result(userResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable("id") long userId,
            UserCreateRequest userCreateRequest) {
        UserResponse userResponse = userService.updateUser(userId, userCreateRequest);
        return ApiResponse.<UserResponse>builder()
                .message("Update user success")
                .result(userResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable("id") long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder()
                .message("Delete user success")
                .build();
    }
}