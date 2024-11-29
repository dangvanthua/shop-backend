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
    PERMISSION_EXISTED(1006, "Permission already exist", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(1007, "Permission is not exist", HttpStatus.NOT_FOUND),
    INVALID_PERMISSION(1008, "Invalid permission IDs provided", HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(1009, "Role already existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1010, "Role not exist", HttpStatus.NOT_FOUND),
    PHONE_NUMBER_EXISTED(1011, "Phone number is already existed", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1012, "Email is already existed", HttpStatus.BAD_REQUEST),
    UPLOAD_FILE_FAILED(1013, "Upload file failed", HttpStatus.BAD_REQUEST),
    DELETE_FILE_FAILED(1014, "Delete file failed", HttpStatus.BAD_REQUEST),
    PARENT_CATE_NOT_EXISTED(1015, "Parent category not existed", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1016, "Category already exist", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1017, "Category not exist", HttpStatus.NOT_FOUND),
    CATEGORY_HAS_SUBCATEGORIES(1018, "Category has existed subcategories", HttpStatus.BAD_REQUEST),
    CATEGORY_HAS_PRODUCTS(1019, "Category has existed products", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_EXISTED(1020, "Attribute already exist", HttpStatus.BAD_REQUEST),
    ATTRIBUTE_NOT_EXIST(1021, "Attribute has not existed", HttpStatus.NOT_FOUND),
    INVALID_ATTRIBUTE_ID_LIST(1022, "Invalid attribute list id", HttpStatus.BAD_REQUEST),
    INVALID_ATTRIBUTE_ID(1023, "Invalid attribute id", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
