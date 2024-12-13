package com.thuan.shop_backend.service.auth;

import com.nimbusds.jose.JOSEException;
import com.thuan.shop_backend.dto.request.auth.AuthenticationRequest;
import com.thuan.shop_backend.dto.request.auth.IntrospectRequest;
import com.thuan.shop_backend.dto.request.auth.RefreshRequest;
import com.thuan.shop_backend.dto.response.auth.AuthenticationResponse;
import com.thuan.shop_backend.dto.response.auth.IntrospectResponse;

import java.text.ParseException;

public interface IAuthService {
    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws ParseException, JOSEException;
    AuthenticationResponse authenticate(AuthenticationRequest auth);
    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
