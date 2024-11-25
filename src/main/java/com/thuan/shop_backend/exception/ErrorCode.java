package com.thuan.shop_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "You do not have permission", HttpStatus.FORBIDDEN),
    PERMISSION_EXISTED(1006, "Permission is already exist", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(1007, "Permission is not exist", HttpStatus.NOT_FOUND),
    INVALID_PERMISSION(1008, "Invalid permission IDs provided", HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(1009, "Role is already existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1010, "Role is not exist", HttpStatus.NOT_FOUND);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
