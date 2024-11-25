package com.thuan.shop_backend.controller;

import com.thuan.shop_backend.dto.request.RoleRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.RoleResponse;
import com.thuan.shop_backend.service.role.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
