package com.thuan.shop_backend.component;

import com.nimbusds.jose.JOSEException;
import com.thuan.shop_backend.dto.request.auth.IntrospectRequest;
import com.thuan.shop_backend.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private AuthService authService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {

            IntrospectRequest introspectRequest = IntrospectRequest.builder()
                    .token(token)
                    .build();

            var response = authService.introspect(introspectRequest);

            if(!response.isValid()) {
                throw new JwtException("Invalid token");
            }

        } catch (ParseException | JOSEException e) {
            throw new JwtException(e.getMessage());
        }

        if(Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
