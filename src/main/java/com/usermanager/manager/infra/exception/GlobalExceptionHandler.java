package com.usermanager.manager.infra.exception;


import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.usermanager.manager.dto.common.ResponseMessage;
import com.usermanager.manager.exception.authentication.TokenInvalid;
import com.usermanager.manager.exception.authentication.TokenNotFoundException;
import com.usermanager.manager.exception.user.UserExistsException;
import com.usermanager.manager.exception.user.UserNotEnabledException;
import com.usermanager.manager.exception.user.UserNotFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ResponseMessage> handleUserExistsException(UserExistsException ex) {
        return ResponseEntity.status(409).body(new ResponseMessage("User already exists: " + ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleUserDoesNotExistsException(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(new ResponseMessage("User not found with login:  " + ex.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(404).body(new ResponseMessage("Username not found " + ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseMessage> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(new ResponseMessage(ex.getMessage()));
    }

    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<ResponseMessage> handleUserNotEnabledException(UserNotEnabledException ex) {
        return ResponseEntity.status(401).body(new ResponseMessage("Please activate the account. " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(Exception ex) {
        return ResponseEntity.status(500).body(new ResponseMessage("Error: " + ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(500).body(new ResponseMessage("Error: " + ex.getMessage()));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleTokenNotFoundException(TokenNotFoundException ex) {
        return ResponseEntity.status(404).body(new ResponseMessage("Token error: " + ex.getMessage()));
    }

    @ExceptionHandler(TokenInvalid.class)
    public ResponseEntity<ResponseMessage> handleTokenInvalid(TokenInvalid ex) {
        return ResponseEntity.status(410).body(new ResponseMessage("Token expired or invalid: " + ex.getMessage()));
    }
}
