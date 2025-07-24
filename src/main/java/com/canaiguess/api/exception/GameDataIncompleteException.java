package com.canaiguess.api.exception;

public class GameDataIncompleteException extends RuntimeException {
    public GameDataIncompleteException(String message) {
        super(message);
    }
}
