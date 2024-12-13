package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.permission.PermissionRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.permisson.PermissionResponse;
import com.thuan.shop_backend.entity.Permission;
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
    public ApiResponse<Permission> createPermission(
            @Valid @RequestBody PermissionRequest permissionRequest) {
        Permission permission = permissionService
                .createPermission(permissionRequest);
        return ApiResponse.<Permission>builder()
                .message("Create permission success")
                .result(permission)
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
    public ApiResponse<Permission> updatePermission(
            @PathVariable("id") long permissionId,
            @Valid @RequestBody PermissionRequest permissionRequest) {
        Permission permission = permissionService.updatePermission(
                permissionId, permissionRequest);
        return ApiResponse.<Permission>builder()
                .message("Update permission success")
                .result(permission)
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