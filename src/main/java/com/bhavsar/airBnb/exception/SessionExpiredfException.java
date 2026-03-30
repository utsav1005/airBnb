package com.bhavsar.airBnb.exception;

public class SessionExpiredfException extends RuntimeException {
    public SessionExpiredfException(String message) {
        super(message);
    }
}
