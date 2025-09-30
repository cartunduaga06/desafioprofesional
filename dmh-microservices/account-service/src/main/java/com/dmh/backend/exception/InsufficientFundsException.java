package com.dmh.backend.exception;

/**
 * Se lanza cuando una cuenta no tiene saldo suficiente para completar una operación.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}