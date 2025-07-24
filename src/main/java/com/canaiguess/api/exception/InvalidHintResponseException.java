package com.canaiguess.api.exception;

public class InvalidHintResponseException extends RuntimeException {
    public InvalidHintResponseException(String message) {
        super(message);
    }

    public InvalidHintResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
