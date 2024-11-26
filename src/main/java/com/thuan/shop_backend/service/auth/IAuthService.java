package com.thuan.shop_backend.service.auth;

import com.nimbusds.jose.JOSEException;
import com.thuan.shop_backend.dto.request.AuthenticationRequest;
import com.thuan.shop_backend.dto.request.IntrospectRequest;
import com.thuan.shop_backend.dto.request.RefreshRequest;
import com.thuan.shop_backend.dto.response.AuthenticationResponse;
import com.thuan.shop_backend.dto.response.IntrospectResponse;

import java.text.ParseException;

public interface IAuthService {
    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws ParseException, JOSEException;
    AuthenticationResponse authenticate(AuthenticationRequest auth);
    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
