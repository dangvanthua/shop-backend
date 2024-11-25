package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.RoleRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.RoleResponse;
import com.thuan.shop_backend.service.role.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid RoleRequest request) {
        RoleResponse roleResponse = roleService.createRole(request);
        return ApiResponse.<RoleResponse>builder()
                .message("Create role success")
                .result(roleResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roleResponses = roleService.getAllRoles();
        return ApiResponse.<List<RoleResponse>>builder()
                .message("Get all roles success")
                .result(roleResponses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getRole(@PathVariable("id") long roleId) {
        RoleResponse roleResponse = roleService.getRole(roleId);
        return ApiResponse.<RoleResponse>builder()
                .message("Get role success")
                .result(roleResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> updateRole(
            @PathVariable("id") long roleId,
            @RequestBody RoleRequest roleRequest) {
        RoleResponse roleResponse = roleService.updateRole(roleId, roleRequest);
        return ApiResponse.<RoleResponse>builder()
                .message("Update role success")
                .result(roleResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable("id") long roleId) {
        roleService.deleteRole(roleId);
        return ApiResponse.<Void>builder()
                .message("Delete role success")
                .build();
    }
}