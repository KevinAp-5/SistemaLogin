package com.usermanager.manager.exception;

public class UserNotEnabledException extends RuntimeException{
    public UserNotEnabledException(String message) {
        super(message);
    }
}
