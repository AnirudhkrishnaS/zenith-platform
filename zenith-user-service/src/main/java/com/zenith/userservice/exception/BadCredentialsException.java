package com.zenith.userservice.exception;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException() {
        super("Invalid email or password");
    }
}
