package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.component.FilePathComponent;
import com.thuan.shop_backend.dto.request.user.UserCreateRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.user.UserResponse;
import com.thuan.shop_backend.entity.User;
import com.thuan.shop_backend.service.file.IFileService;
import com.thuan.shop_backend.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final IFileService fileService;
    private final FilePathComponent filePathComponent;

    @PostMapping
    public ApiResponse<User> createUser(
            @Valid @RequestBody UserCreateRequest userCreateRequest) {
        User user = userService.createUser(userCreateRequest);
        return ApiResponse.<User>builder()
                .message("Create user success")
                .result(user)
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

    @GetMapping("/detail")
    public ApiResponse<UserResponse> getUserDetail() {
        UserResponse user = userService.getUserDetail();
        return ApiResponse.<UserResponse>builder()
                .message("Get user info detail success")
                .result(user)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(
            @PathVariable("id") long userId,
            UserCreateRequest userCreateRequest) {
        User user = userService.updateUser(userId, userCreateRequest);
        return ApiResponse.<User>builder()
                .message("Update user success")
                .result(user)
                .build();
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateUser(@PathVariable("id") long userId) {
        userService.deactivateUser(userId);
        return ApiResponse.<Void>builder()
                .message("Block user account success")
                .build();
    }

    // this function will fix because user not allow to delete
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable("id") long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder()
                .message("Delete user success")
                .build();
    }

    @PostMapping("/avatar/{id}")
    public ApiResponse<Void> uploadAvatarImage(
            @PathVariable("id") long userId,
            @RequestParam("file") MultipartFile file) {

        String folderName = filePathComponent.getUserAvatarPath();
        Map uploadResult = fileService.uploadFile(file, folderName);
        String publicId = (String) uploadResult.get("public_id");
        String avatarUrl = (String) uploadResult.get("url");
        userService.uploadAvatarUser(userId, publicId, avatarUrl);

        return ApiResponse.<Void>builder()
                .message("Upload avatar image success")
                .build();
    }

    @GetMapping("/by-user-id")
    public ApiResponse<List<UserResponse>> getSellersByUserId() {
        List<UserResponse> userResponses = userService.getSellerByUserId();
        return ApiResponse.<List<UserResponse>>builder()
                .message("Get user from seller success")
                .result(userResponses)
                .build();
    }
}