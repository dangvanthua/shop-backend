package com.thuan.shop_backend.controller;

import com.nimbusds.jose.JOSEException;
import com.thuan.shop_backend.dto.request.auth.AuthenticationRequest;
import com.thuan.shop_backend.dto.request.auth.IntrospectRequest;
import com.thuan.shop_backend.dto.request.auth.RefreshRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.auth.AuthenticationResponse;
import com.thuan.shop_backend.dto.response.auth.IntrospectResponse;
import com.thuan.shop_backend.service.auth.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        var result = authService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(
            @RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}