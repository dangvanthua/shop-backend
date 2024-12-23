package com.thuan.shop_backend.controller;

import com.nimbusds.jose.JOSEException;
import com.thuan.shop_backend.dto.request.auth.AuthenticationRequest;
import com.thuan.shop_backend.dto.request.auth.IntrospectRequest;
import com.thuan.shop_backend.dto.request.auth.RefreshRequest;
import com.thuan.shop_backend.dto.request.user.UserCreateRequest;
import com.thuan.shop_backend.dto.request.user.UserLoginRequest;
import com.thuan.shop_backend.dto.response.ApiResponse;
import com.thuan.shop_backend.dto.response.auth.AuthenticationResponse;
import com.thuan.shop_backend.dto.response.auth.IntrospectResponse;
import com.thuan.shop_backend.entity.User;
import com.thuan.shop_backend.exception.AppException;
import com.thuan.shop_backend.exception.ErrorCode;
import com.thuan.shop_backend.service.auth.IAuthService;
import com.thuan.shop_backend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final IUserService userService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        var result = authService.authenticate(request, false);
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

    @GetMapping("/social-login")
    public ApiResponse<String> socialAuth(
            @RequestParam("login_type") String loginType) {
        loginType = loginType.trim().toLowerCase();
        String url = authService.generateAuthUrl(loginType);
        return ApiResponse.<String>builder()
                .message("Return link login success")
                .result(url)
                .build();
    }

    @GetMapping("/social/callback")
    public ApiResponse<AuthenticationResponse> callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType
    ) throws IOException {

        Map<String, Object> userInfo = authService.authenticationAndFetchProfile(code, loginType);
        if(userInfo == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String accountId = "";
        String name = "";
        String picture = "";
        String email = "";

        if(loginType.trim().equals("google")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("sub"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            picture = (String) Objects.requireNonNullElse(userInfo.get("picture"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
        }else if(loginType.trim().equals("facebook")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("id"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
            Object pictureObj = userInfo.get("picture");
            if(pictureObj instanceof Map) {
                Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
                Object dataObj = pictureData.get("data");
                if (dataObj instanceof Map) {
                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                    Object urlObj = dataMap.get("url");
                    if (urlObj instanceof String) {
                        picture = (String) urlObj;
                    }
                }
            }
        }

        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .fullName(name)
                .email(email)
                .picture(picture)
                .providerId(accountId)
                .providerName(loginType)
                .build();

        User user = userService.createUserSocialAccount(userLoginRequest);

        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(user.getEmail())
                .build();

        var authResponse = authService.authenticate(authenticationRequest, true);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login social account google")
                .result(authResponse)
                .build();
    }
}