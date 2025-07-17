package com.canaiguess.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.canaiguess.api.exception.BusinessErrorCodes.BAD_CREDENTIALS;
import static com.canaiguess.api.exception.BusinessErrorCodes.RESOURCE_ALREADY_IN_USE;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ExceptionResponse> handleException(DuplicateResourceException exp) {
        return ResponseEntity
                .status(CONFLICT)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(RESOURCE_ALREADY_IN_USE.getCode())
                                .businessErrorDescription(RESOURCE_ALREADY_IN_USE.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(err -> {
                    var errorMessage = err.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        // 2DO log the exception
        exp.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal error")
                                .error(exp.getMessage())
                                .build()
                );
    }
}
