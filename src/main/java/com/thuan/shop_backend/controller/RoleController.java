package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.role.RoleRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.role.RoleResponse;
import com.thuan.shop_backend.entity.Role;
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
    public ApiResponse<Role> createRole(
            @Valid @RequestBody RoleRequest request) {
        Role role = roleService.createRole(request);
        return ApiResponse.<Role>builder()
                .message("Create role success")
                .result(role)
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
    public ApiResponse<Role> updateRole(
            @PathVariable("id") long roleId,
            @Valid @RequestBody RoleRequest roleRequest) {
        Role role = roleService.updateRole(roleId, roleRequest);
        return ApiResponse.<Role>builder()
                .message("Update role success")
                .result(role)
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