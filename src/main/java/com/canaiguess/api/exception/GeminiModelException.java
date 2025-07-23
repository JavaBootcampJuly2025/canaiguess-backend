package com.canaiguess.api.exception;

public class GeminiModelException extends RuntimeException {
    public GeminiModelException(String message) {
        super(message);
    }

    public GeminiModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
