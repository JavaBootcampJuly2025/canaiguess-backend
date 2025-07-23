package com.canaiguess.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "New password does not match"),
    BAD_CREDENTIALS(302, FORBIDDEN, "Username or password is incorrect"),
    RESOURCE_ALREADY_IN_USE(303, CONFLICT, "Resource is already in use"),
    UNAUTHORIZED_ACCESS(304, UNAUTHORIZED, "Unauthorized access"),
    ;

    @Getter
    private final int code;
    @Getter
    private final HttpStatus httpStatus;
    @Getter
    private final String description;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
