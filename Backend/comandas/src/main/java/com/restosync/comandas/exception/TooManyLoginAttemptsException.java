package com.restosync.comandas.exception;

public class TooManyLoginAttemptsException extends RuntimeException {

    public TooManyLoginAttemptsException() {
        super("Demasiados intentos fallidos. Intente nuevamente mas tarde.");
    }
}
