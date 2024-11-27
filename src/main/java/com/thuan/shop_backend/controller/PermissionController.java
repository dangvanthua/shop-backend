package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.PermissionRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.PermissionResponse;
import com.thuan.shop_backend.service.permission.IPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(
            @Valid @RequestBody PermissionRequest permissionRequest) {
        PermissionResponse permissionResponse = permissionService
                .createPermission(permissionRequest);
        return ApiResponse.<PermissionResponse>builder()
                .message("Create permission success")
                .result(permissionResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermission() {
        List<PermissionResponse> permissionResponses = permissionService.getAllPermission();
        return ApiResponse.<List<PermissionResponse>>builder()
                .message("Get all permission success")
                .result(permissionResponses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PermissionResponse> getPermission(
            @PathVariable("id") long permissionId) {
        PermissionResponse permissionResponse = permissionService
                .getPermissionById(permissionId);
        return ApiResponse.<PermissionResponse>builder()
                .message("Get permission success")
                .result(permissionResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable("id") long permissionId,
            @Valid @RequestBody PermissionRequest permissionRequest) {
        PermissionResponse permissionResponse = permissionService.updatePermission(
                permissionId, permissionRequest);
        return ApiResponse.<PermissionResponse>builder()
                .message("Update permission success")
                .result(permissionResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(
            @PathVariable("id") long permissionId) {
        permissionService.deletePermission(permissionId);
        return ApiResponse.<Void>builder()
                .message("Delete permission success")
                .build();
    }
}